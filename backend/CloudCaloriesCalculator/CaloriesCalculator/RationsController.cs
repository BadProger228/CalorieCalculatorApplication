using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using System.Data;

namespace CaloriesCalculator
{

    [ApiController]
    [Route("api/saved-rations")]
    public class SavedRationsController : ControllerBase
    {
        private readonly string _cs;

        public SavedRationsController(IConfiguration cfg)
        {
            _cs = cfg.GetConnectionString("DefaultConnection")!;
        }

        [HttpPost("create")]
        public IActionResult Create(CreateSavedRationRequest req)
        {
            if (string.IsNullOrWhiteSpace(req.Name))
                return BadRequest("Name is required.");

            using var con = new SqlConnection(_cs);
            con.Open();
            using var tx = con.BeginTransaction();

            try
            {
                var createCmd = new SqlCommand(@"
                INSERT INTO saved_rations (user_id, name, created_at)
                OUTPUT INSERTED.id, INSERTED.created_at
                VALUES (@u, @n, SYSUTCDATETIME());
            ", con, tx);

                createCmd.Parameters.Add("@u", SqlDbType.Int).Value = req.UserId;
                createCmd.Parameters.Add("@n", SqlDbType.NVarChar, 100).Value = req.Name.Trim();

                int savedRationId;
                DateTime createdAt;

                using (var r = createCmd.ExecuteReader())
                {
                    r.Read();
                    savedRationId = r.GetInt32(0);
                    createdAt = r.GetDateTime(1);
                }

                var itemCmd = new SqlCommand(@"
                INSERT INTO saved_ration_items
                (saved_ration_id, food_name, calories, protein, fat, carbs, weight_g)
                VALUES (@rid, @fn, @cal, @p, @f, @c, @w);
            ", con, tx);

                itemCmd.Parameters.Add("@rid", SqlDbType.Int);
                itemCmd.Parameters.Add("@fn", SqlDbType.NVarChar, 200);
                itemCmd.Parameters.Add("@cal", SqlDbType.Int);
                itemCmd.Parameters.Add("@p", SqlDbType.Float);
                itemCmd.Parameters.Add("@f", SqlDbType.Float);
                itemCmd.Parameters.Add("@c", SqlDbType.Float);
                itemCmd.Parameters.Add("@w", SqlDbType.Int);

                int totalCalories = 0;

                foreach (var it in req.Items)
                {
                    if (string.IsNullOrWhiteSpace(it.FoodName))
                        continue;

                    itemCmd.Parameters["@rid"].Value = Convert.ToInt32(savedRationId);
                    itemCmd.Parameters["@fn"].Value = it.FoodName.Trim();
                    itemCmd.Parameters["@cal"].Value = Convert.ToInt32(it.Calories);
                    itemCmd.Parameters["@p"].Value = Convert.ToInt32(it.Protein);
                    itemCmd.Parameters["@f"].Value = Convert.ToInt32(it.Fat);
                    itemCmd.Parameters["@c"].Value = Convert.ToInt32(it.Carbs);
                    itemCmd.Parameters["@w"].Value = Convert.ToInt32(it.WeightGrams);

                    itemCmd.ExecuteNonQuery();
                    totalCalories += (int)it.Calories;
                }

                tx.Commit();

                foreach (var it in req.Items)
                {
                    Console.WriteLine(
                        $"{it.FoodName} | w={it.WeightGrams} | cal={it.Calories}"
                    );
                }

                return Ok(new
                {
                    id = savedRationId,
                    userId = req.UserId,
                    name = req.Name.Trim(),
                    createdAt,
                    totalCalories
                });


            }
            catch (SqlException ex)
            {
                tx.Rollback();

                if (ex.Number == 2601 || ex.Number == 2627)
                    return Conflict("Saved ration with this name already exists for this user.");

                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                tx.Rollback();
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("by-name")]
        public IActionResult GetByName([FromQuery] int userId, [FromQuery] string name)
        {
            if (string.IsNullOrWhiteSpace(name))
                return BadRequest("Name is required.");

            using var con = new SqlConnection(_cs);
            con.Open();

            var headCmd = new SqlCommand(@"
            SELECT TOP 1 id, user_id, name, created_at
            FROM saved_rations
            WHERE user_id = @u AND name = @n;
        ", con);

            headCmd.Parameters.Add("@u", SqlDbType.Int).Value = userId;
            headCmd.Parameters.Add("@n", SqlDbType.NVarChar, 100).Value = name.Trim();

            int savedRationId;
            DateTime createdAt;
            string realName;

            using (var r = headCmd.ExecuteReader())
            {
                if (!r.Read())
                    return NotFound("Saved ration not found.");

                savedRationId = r.GetInt32(0);
                createdAt = r.GetDateTime(3);
                realName = r.GetString(2);
            }

            var itemsCmd = new SqlCommand(@"
            SELECT food_name, calories, protein, fat, carbs
            FROM saved_ration_items
            WHERE saved_ration_id = @rid
            ORDER BY id;
        ", con);

            itemsCmd.Parameters.Add("@rid", SqlDbType.Int).Value = savedRationId;

            var items = new List<FoodItemDto>();
            int totalCalories = 0;

            using (var ir = itemsCmd.ExecuteReader())
            {
                while (ir.Read())
                {
                    var item = new FoodItemDto
                    {
                        FoodName = ir.GetString(0),
                        Calories = ir.GetInt32(1),
                        Protein = ir.GetDouble(2),
                        Fat = ir.GetDouble(3),
                        Carbs = ir.GetDouble(4),
                    };
                    totalCalories += (int)item.Calories;
                    items.Add(item);
                }
            }

            return Ok(new SavedRationResponse
            {
                Id = savedRationId,
                UserId = userId,
                Name = realName,
                CreatedAt = createdAt,
                TotalCalories = totalCalories,
                Items = items
            });
        }

        [HttpGet("list")]
        public IActionResult List(int userId)
        {
            using var con = new SqlConnection(_cs);
            con.Open();

            var cmd = new SqlCommand(@"
                SELECT r.id,
                       r.name,
                       r.created_at,
                       CAST(ISNULL(SUM(i.calories), 0) AS INT) AS totalCalories
                FROM saved_rations r
                LEFT JOIN saved_ration_items i ON i.saved_ration_id = r.id
                WHERE r.user_id = @u
                GROUP BY r.id, r.name, r.created_at
                ORDER BY r.created_at DESC
            ", con);

            cmd.Parameters.AddWithValue("@u", userId);

            var list = new List<object>();
            using var r = cmd.ExecuteReader();
            while (r.Read())
            {
                list.Add(new
                {
                    id = r.GetInt32(0),
                    name = r.GetString(1),
                    createdAt = r.GetDateTime(2),
                    totalCalories = r.GetInt32(3)
                });
            }

            return Ok(list);
        }

        [HttpGet("{id}")]
        public IActionResult GetById(int id)
        {
            using var con = new SqlConnection(_cs);
            con.Open();

            var headCmd = new SqlCommand(@"
        SELECT id, name, created_at
        FROM saved_rations
        WHERE id = @id
    ", con);

            headCmd.Parameters.AddWithValue("@id", id);

            string name;
            DateTime createdAt;

            using (var r = headCmd.ExecuteReader())
            {
                if (!r.Read())
                    return NotFound();

                name = r.GetString(1);
                createdAt = r.GetDateTime(2);
            }

            var itemsCmd = new SqlCommand(@"
        SELECT food_name, calories, protein, fat, carbs, weight_g
        FROM saved_ration_items
        WHERE saved_ration_id = @id
        ORDER BY id
    ", con);

            itemsCmd.Parameters.AddWithValue("@id", id);

            var items = new List<FoodItemDto>();
            int totalCalories = 0;

            using var ir = itemsCmd.ExecuteReader();
            while (ir.Read())
            {
                var item = new FoodItemDto
                {
                    FoodName = ir.GetString(0),
                    Calories = Convert.ToInt32(ir.GetDouble(1)),
                    Protein = ir.GetDouble(2),
                    Fat = ir.GetDouble(3),
                    Carbs = ir.GetDouble(4),
                    WeightGrams = ir.GetInt32(5)
                };

                totalCalories += (int)item.Calories;
                items.Add(item);
            }

            return Ok(new
            {
                id,
                name,
                createdAt,
                totalCalories,
                items
            });
        }


        [HttpDelete("delete")]
        public IActionResult Delete([FromQuery] int userId, [FromQuery] string name)
        {
            if (string.IsNullOrWhiteSpace(name))
                return BadRequest("Name is required.");

            using var con = new SqlConnection(_cs);
            con.Open();

            var cmd = new SqlCommand(@"
            DELETE FROM saved_rations
            WHERE user_id = @u AND name = @n;
        ", con);

            cmd.Parameters.Add("@u", SqlDbType.Int).Value = userId;
            cmd.Parameters.Add("@n", SqlDbType.NVarChar, 100).Value = name.Trim();

            int affected = cmd.ExecuteNonQuery();
            if (affected == 0)
                return NotFound("Saved ration not found.");

            return Ok("Deleted");
        }
    }

}
