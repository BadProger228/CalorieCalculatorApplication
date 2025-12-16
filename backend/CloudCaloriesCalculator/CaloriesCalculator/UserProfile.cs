namespace CaloriesCalculator
{
    public class UserProfile
    {
        public int UserId { get; set; }
        public string FirstName { get; set; } = null!;
        public string LastName { get; set; } = null!;
        public int HeightCm { get; set; }
        public int WeightKg { get; set; }
        public string ActivityLevel { get; set; } = null!;
    }
}
