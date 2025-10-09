# Java Gemini Telegram Bot

A multi-threaded Telegram bot built with Java that uses Google's Gemini Pro to provide intelligent, conversational responses. The bot maintains a unique conversation history for each user, allowing for follow-up questions.

## Features

- **Gemini Integration**: Connects to the Gemini API to generate dynamic and context-aware responses.
- **Conversation History**: Remembers the context of the conversation for each individual user.
- **Multi-threaded**: Uses a thread pool (`ExecutorService`) to handle multiple users simultaneously, ensuring the bot remains responsive even under load.
- **Maven-based**: A clean, standard Maven project that is easy to build and run.

## Prerequisites

Before you begin, ensure you have the following installed:
- [Java Development Kit (JDK) 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or higher
- [Apache Maven](https://maven.apache.org/download.cgi)

## Setup and Configuration

Follow these steps to get the bot up and running.

### 1. Clone the Repository

```bash
git clone https://github.com/chelipika/gemini-telegram-bot-Java.git
cd gemini-telegram-bot-Java
```

### 2. Set Environment Variables

This project requires two secret keys to function. It is crucial that you **do not** write these keys directly in the code. Set them as environment variables.

**Required Variables:**
- `TELEGRAM_BOT_TOKEN`: Your token from Telegram's @BotFather.
- `GEMINI_API_KEY`: Your API key from Google AI Studio.

**On Linux/macOS:**
```bash
export TELEGRAM_BOT_TOKEN="12345:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"
export GEMINI_API_KEY="AIzaSyA...your...key...here..."
```
*(You can add these lines to your `~/.bashrc` or `~/.zshrc` file to make them permanent.)*

**On Windows (Command Prompt):**
```cmd
set TELEGRAM_BOT_TOKEN="12345:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"
set GEMINI_API_KEY="AIzaSyA...your...key...here..."
```

**On Windows (PowerShell):**
```powershell
$env:TELEGRAM_BOT_TOKEN="12345:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"
$env:GEMINI_API_KEY="AIzaSyA...your...key...here..."```

### 3. Build the Project

Use Maven to compile the code and download all the necessary dependencies.

```bash
mvn clean install
```

## Running the Bot

Once the project is built and your environment variables are set, you can run the bot with the following Maven command:

```bash
mvn exec:java
```

You should see a "Bot is running..." message in your console. You can now start sending messages to your bot on Telegram!````

Now your project is secure, clean, and well-documented for publishing on GitHub.
