# Soho

A serverless web countdown application that displays time remaining until a target date, with timezone support and real-time updates.

- [x] **[Valentine's Day 💕](https://9-spades-soho.s3.us-west-1.amazonaws.com/src/index.html?targetDate=2025-02-14T00:00:00&timezone=America/New_York)**
- [ ] **[Halloween 🎃](https://9-spades-soho.s3.us-west-1.amazonaws.com/src/index.html?targetDate=2025-10-31T00:00:00&timezone=America/New_York)**
- [ ] **[Xmas 🎄](https://9-spades-soho.s3.us-west-1.amazonaws.com/src/index.html?targetDate=2025-12-25T00:00:00&timezone=America/New_York)**

## Overview

This project consists of:
- **Frontend**– Static HTML/CSS/JavaScript countdown display
- **Backend**– AWS Lambda function for countdown calculations
- **Infrastructure**– Deployed on AWS S3 (frontend) and Lambda (API)

## Features

- Real-time countdown display (days, hours, minutes, seconds)
- Timezone support for accurate calculations
- Clean, responsive UI with gradient background
- CORS-enabled API for cross-origin requests
- Comprehensive error handling and validation

## Usage

### Web Interface

Access the countdown via S3-hosted frontend
> ```
> https://9-spades-soho.s3.us-west-1.amazonaws.com/src/index.html?targetDate=2025-12-25T00:00:00&timezone=America/New_York
> ```

**Query Parameters**
- `targetDate` (required): Target date in ISO format (YYYY-MM-DDTHH:mm:ss)
- `timezone` (optional): Timezone identifier (defaults to UTC)

Examples
```
?targetDate=2025-12-25T00:00:00&timezone=America/New_York
?targetDate=2025-12-25T00:00:00&timezone=Europe/London
?targetDate=2025-12-25T00:00:00
```

### API Endpoint

Direct API access for programmatic usage
> ```
> https://5qa6wrp7aej62k677izcvgku7e0qcqqv.lambda-url.us-west-1.on.aws/?targetDate=2025-12-25T00:00:00&timezone=America/New_York
> ```

**Response Format**
```json
{
  "days": 118,
  "hours": 7,
  "minutes": 30,
  "seconds": 45,
  "targetDate": "2025-12-25T00:00:00",
  "timezone": "America/New_York"
}
```

## Development

### Prerequisites
- Java 21
- Gradle

### Test & Build
```bash
cd back
./gradlew test          # Run tests
./gradlew shadowJar     # Build fat JAR for deployment
```

### Project Structure
```
soho/
├── front/              # Static web frontend
│   └── src/
│       ├── index.html
│       ├── scripts/main.js
│       └── styles/style.css
└── back/               # Java Lambda backend
    └── app/
        ├── build.gradle
        └── src/
            ├── main/java/nine/spades/
            │   ├── soho/aws/          # Lambda handlers
            │   ├── time/              # Countdown logic
            │   └── utils/             # API utilities
            └── test/java/             # Test suites
```
