# spring-boot-ai-demo
Demo Application for AI Chat Bot with Spring Boot, WebSocket, Angular

## Getting Started

You first need to run Ollama on your local PC. Go to 
[Ollama Github site](https://github.com/ollama/ollama)
to learn how to download and setup Ollama locally.

Then follow the setup instructions to start backend and frontend servers:

- [Backend API Server Setup Instructions](/backend-api/README.md)
- [Frontend Web UI Setup Instructions](/frontend-web/README.md)

If everything goes well, you can open [http://localhost:8081](http://localhost:8081)
in any modern browser. You can register a new user account with email and password.
From API server log, find the activation url, 
and paste the url to the browser to activate your account.
You can use the email and password to login, and start chatting with a local LLM!

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
