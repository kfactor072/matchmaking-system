# Matchmaking System

A RESTful API backend service for managing player matchmaking with an ELO-based rating system. Built with Spring Boot, this system tracks players, records match results, and automatically updates player ratings using the ELO algorithm.

## 🎯 Features

- **Player Management**: Create, retrieve, update, and delete players
- **Match Recording**: Record match results between players
- **ELO Rating System**: Automatic rating calculations using K-factor algorithm
- **Leaderboard**: View top players ranked by rating
- **Player Statistics**: Track wins, losses, win rate, and match history
- **Interactive Dashboard**: Clean web interface for managing players and matches

## 🛠️ Technologies Used

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: JUnit 5, Mockito, MockMvc
- **Build Tool**: Maven
- **Frontend**: Vanilla JavaScript, HTML5, CSS3

## 📊 Test Coverage

- **Overall Coverage**: 92%
- **Services**: 100%
- **Controllers**: 100%
- **Branch Coverage**: 100%

Total: **20+ unit and integration tests** covering all business logic and API endpoints.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. Clone the repository
```bash
git clone https://github.com/yourusername/matchmaking-system.git
cd matchmaking-system
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Accessing the Dashboard

Open `dashboard.html` in your browser to access the interactive web interface.

### Accessing the H2 Console

Navigate to `http://localhost:8080/h2-console` and use:
- **JDBC URL**: `jdbc:h2:mem:matchmaking`
- **Username**: `sa`
- **Password**: (leave blank)

## 📖 API Documentation

### Player Endpoints

#### Create Player
```http
POST /api/players
Content-Type: application/json

{
  "username": "alice"
}
```

#### Get All Players
```http
GET /api/players
```

#### Get Player by ID
```http
GET /api/players/{id}
```

#### Get Player by Username
```http
GET /api/players/username/{username}
```

#### Get Leaderboard
```http
GET /api/players/leaderboard?limit=10
```

#### Get Player Statistics
```http
GET /api/players/{id}/stats
```

#### Delete Player
```http
DELETE /api/players/{id}
```

### Match Endpoints

#### Record Match
```http
POST /api/matches
Content-Type: application/json

{
  "playerAId": 1,
  "playerBId": 2,
  "winnerId": 1
}
```

#### Get All Matches
```http
GET /api/matches
```

#### Get Match by ID
```http
GET /api/matches/{id}
```

#### Get Matches for Player
```http
GET /api/matches/player/{playerId}
```

## 🎮 How the ELO System Works

The system uses a K-factor of 32 for rating adjustments:

1. **Expected Score**: Calculated based on rating difference between players
   ```
   E_A = 1 / (1 + 10^((R_B - R_A)/400))
   ```

2. **New Rating**: Updated after each match
   ```
   R'_A = R_A + K × (S_A - E_A)
   ```
   - Where S_A = 1 for win, 0 for loss

3. **Rating Changes**:
   - Winners gain points, losers lose points
   - Upset victories (lower-rated player wins) result in larger rating changes
   - Expected wins result in smaller rating changes

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/kfactor/matchmaking/
│   │   ├── controller/     # REST API endpoints
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── model/          # Entity classes
│   │   ├── dto/            # Data transfer objects
│   │   └── config/         # Configuration classes
│   └── resources/
│       └── application.yml # Application configuration
└── test/
    └── java/com/kfactor/matchmaking/
        ├── controller/     # Integration tests
        └── service/        # Unit tests
```

## 🧪 Running Tests

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn test jacoco:report
```

Coverage report will be available at: `target/site/jacoco/index.html`

## 📝 Example Usage

1. **Create two players:**
```bash
curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{"username":"alice"}'

curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{"username":"bob"}'
```

2. **Record a match (Alice wins):**
```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{"playerAId":1,"playerBId":2,"winnerId":1}'
```

3. **View the leaderboard:**
```bash
curl http://localhost:8080/api/players/leaderboard
```

4. **Check player stats:**
```bash
curl http://localhost:8080/api/players/1/stats
```

## 🎨 Dashboard Features

The included web dashboard provides:
- ✅ Player creation with validation
- ✅ Match recording with smart winner selection
- ✅ Live leaderboard with rankings
- ✅ Player statistics (W/L ratio, win rate)
- ✅ Real-time updates
- ✅ Toast notifications
- ✅ Responsive design

## 🔮 Future Enhancements

Potential improvements for this project:
- [ ] Add authentication/authorization
- [ ] Implement WebSocket for real-time updates
- [ ] Add match history pagination
- [ ] Support for team-based matches
- [ ] Add player profiles with avatars
- [ ] Export statistics to CSV/PDF
- [ ] Add GraphQL API support

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

**Kennedy Joyce**
- GitHub: [@kfactor072](https://github.com/kfactor072)
- LinkedIn: [Kennedy Joyce](https://www.linkedin.com/in/kennedy-joyce-790790275/)

## 🙏 Acknowledgments

- ELO rating system based on Arpad Elo's work
- Spring Boot framework and documentation
- H2 Database Engine

---

**Note**: This is a portfolio project demonstrating backend development skills, RESTful API design, database management, and comprehensive testing practices.
