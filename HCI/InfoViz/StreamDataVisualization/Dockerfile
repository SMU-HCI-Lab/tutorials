FROM python:3.8

RUN pip install --no-cache-dir \
        "uvicorn[standard]" \
        gunicorn \
        starlette \
        sse-starlette \
        jinja2

# COPY ./app /app
WORKDIR /app

EXPOSE 8000

# Run the start script, it will check for an /app/prestart.sh script (e.g. for migrations)
# And then will start Gunicorn with Uvicorn
CMD ["uvicorn", "main:app", "--reload", "--host", "0.0.0.0", "--port", "8000"]
# CMD ["ls"]