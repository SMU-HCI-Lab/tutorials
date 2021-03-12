# From https://github.com/tiangolo/uvicorn-gunicorn-starlette-docker

import asyncio
import random
import sys


from starlette.applications import Starlette
from starlette.responses import JSONResponse

from sse_starlette.sse import EventSourceResponse
from starlette.templating import Jinja2Templates


version = f"{sys.version_info.major}.{sys.version_info.minor}"
templates = Jinja2Templates(directory='./templates')
app = Starlette()


async def numbers():
    num_data = 150
    sleep_time = 30 / num_data
    for i in range(num_data):
        await asyncio.sleep(sleep_time)
        yield dict(data=random.uniform(-1, 1))


@app.route("/")
async def homepage(request):
    message = f"Hello me! From Starlette running on Uvicorn with Gunicorn. Using Python {version}"

    template = "sse-line-chart.html"
    context = {"request": request}
    # return JSONResponse({"message": message})

    return templates.TemplateResponse(template, context)


@app.route("/stream")
async def sse(request):
    generator = numbers()
    return EventSourceResponse(generator)