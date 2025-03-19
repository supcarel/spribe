# Use the official PostgreSQL image from Docker Hub
FROM postgres:latest

# Set environment variables for PostgreSQL
ENV POSTGRES_DB=spribe
ENV POSTGRES_USER=spribe_user
ENV POSTGRES_PASSWORD=spribe_user_password

# Expose the default PostgreSQL port
EXPOSE 5432

# Optionally, copy initialization SQL scripts or other files if needed
# Uncomment the lines below if you have initialization scripts to include

# COPY ./init.sql /docker-entrypoint-initdb.d/

# The default behavior for the postgres image is to start PostgreSQL
CMD ["postgres"]
