FROM openjdk:17-jdk-slim

# Add the source code to the image
COPY . /app

# Set the working directory
WORKDIR /app

# Build the application
# build won't work well
RUN ./gradlew assemble --stacktrace --no-daemon

# Expose the application port
EXPOSE 8080

# Start the application
CMD ./gradlew bootRun --no-daemon
