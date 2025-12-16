namespace CaloriesCalculator
{
    public class RegisterRequest
    {
        public string Login { get; set; } = null!;
        public string Password { get; set; } = null!;
        public string FirstName { get; set; } = null!;
        public string LastName { get; set; } = null!;
        public int HeightCm { get; set; }
        public int WeightKg { get; set; }
        public string ActivityLevel { get; set; } = null!;

    }
}
