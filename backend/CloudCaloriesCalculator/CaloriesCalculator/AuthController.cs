using CaloriesCalculator;
using Microsoft.AspNetCore.Mvc;
using System.Data.SqlClient;
using System.Security.Cryptography;
using System.Text;
using Microsoft.Data.SqlClient;


[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly string _cs;

    public AuthController(IConfiguration cfg)
    {
        _cs = cfg.GetConnectionString("DefaultConnection")!;
    }

    // ✅ REGISTRATION
    [HttpPost("register")]
    public IActionResult Register(RegisterRequest req)
    {
        var salt = PasswordHelper.GenerateSalt();
        var hash = PasswordHelper.HashPassword(req.Password, salt);

        using var con = new SqlConnection(_cs);
        con.Open();

        var tx = con.BeginTransaction();

        try
        {
            var userCmd = new SqlCommand(
                @"INSERT INTO users (login, password_hash, salt, created_at)
                  OUTPUT INSERTED.id
                  VALUES (@l, @p, @s, GETDATE())",
                con, tx);

            userCmd.Parameters.AddWithValue("@l", req.Login);
            userCmd.Parameters.AddWithValue("@p", hash);
            userCmd.Parameters.AddWithValue("@s", salt);

            int userId = (int)userCmd.ExecuteScalar()!;

            var profileCmd = new SqlCommand(
                @"INSERT INTO user_profile
                  (user_id, first_name, last_name, height_cm, weight_kg, activity_level)
                  VALUES (@id,@f,@l,@h,@w,@a)",
                con, tx);

            profileCmd.Parameters.AddWithValue("@id", userId);
            profileCmd.Parameters.AddWithValue("@f", req.FirstName);
            profileCmd.Parameters.AddWithValue("@l", req.LastName);
            profileCmd.Parameters.AddWithValue("@h", req.HeightCm);
            profileCmd.Parameters.AddWithValue("@w", req.WeightKg);
            profileCmd.Parameters.AddWithValue("@a", req.ActivityLevel);

            profileCmd.ExecuteNonQuery();
            tx.Commit();

            return Ok(new { userId });
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.Message);
            tx.Rollback();
            return BadRequest("Registration failed");
        }
    }

    [HttpPost("login")]
    public IActionResult Login([FromBody] LoginRequest request)
    {
        using var connection = new SqlConnection(_cs);
        connection.Open();

        // 1️⃣ Получаем hash + salt по логину
        var cmd = new SqlCommand(@"
           SELECT id, password_hash, salt
           FROM users
           WHERE login = @login
       ", connection);

        cmd.Parameters.AddWithValue("@login", request.Login);

        using var reader = cmd.ExecuteReader();

        if (!reader.Read())
            return Unauthorized("Invalid login");

        int userId = reader.GetInt32(0);
        string storedHash = (string)reader["password_hash"];

        int saltIndex = reader.GetOrdinal("salt");

        if (reader.IsDBNull(saltIndex))
            return StatusCode(500, "Salt is missing in database");

        string salt = (string)reader.GetValue(saltIndex);

        reader.Close();

        // Fix: Convert the hashed password to a byte array
        string inputHash = PasswordHelper.HashPassword(request.Password, salt);

        // 3️⃣ Сравнение
        if (!storedHash.SequenceEqual(inputHash))
            return Unauthorized("Invalid password");

        // 4️⃣ УСПЕХ — возвращаем userId
        return Ok(new LoginResponse
        {
            UserId = userId
        });
    }

    [HttpGet("profile/{userId}")]
    public IActionResult GetProfileByUserId(int userId)
    {
        using var connection = new SqlConnection(_cs);
        connection.Open();

        using var cmd = new SqlCommand(@"
        SELECT user_id, first_name, last_name, height_cm, weight_kg, activity_level
        FROM user_profile
        WHERE user_id = @userId
    ", connection);

        cmd.Parameters.Add("@userId", System.Data.SqlDbType.Int).Value = userId;

        using var reader = cmd.ExecuteReader();

        if (!reader.Read())
            return NotFound("Profile not found");

        var dto = new UserProfile
        {
            UserId = reader.GetInt32(reader.GetOrdinal("user_id")),
            FirstName = reader.GetString(reader.GetOrdinal("first_name")),
            LastName = reader.GetString(reader.GetOrdinal("last_name")),
            HeightCm = reader.GetInt32(reader.GetOrdinal("height_cm")),
            WeightKg = reader.GetInt32(reader.GetOrdinal("weight_kg")),
            ActivityLevel = reader.GetString(reader.GetOrdinal("activity_level"))
        };

        return Ok(dto);
    }


    // ❌ DELETE PROFILE
    [HttpDelete("delete/{userId}")]
    public IActionResult DeleteProfile(int userId)
    {
        using var con = new SqlConnection(_cs);
        con.Open();

        var cmd = new SqlCommand(
            @"DELETE FROM ration_items WHERE ration_id IN
                (SELECT id FROM daily_rations WHERE user_id=@id);
              DELETE FROM daily_rations WHERE user_id=@id;
              DELETE FROM user_profile WHERE user_id=@id;
              DELETE FROM users WHERE id=@id;",
            con);

        cmd.Parameters.AddWithValue("@id", userId);
        cmd.ExecuteNonQuery();

        return Ok("Profile deleted");
    }
}

