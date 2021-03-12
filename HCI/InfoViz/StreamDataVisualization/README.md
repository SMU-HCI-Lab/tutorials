# Stream Data Visualization

Sample code for stream data visualization.


## Moving Line Chart

For the Moving Line Chart exercises, run `python -m http.server` to start a local server. Then access [`localhost:8000`](http://localhost:8000).


## Visualize Data Sent via SSE 

For the Visualize Data Sent via SSE excercise, run the following commands to send data via SSE. 

```
docker build -t starlette .

docker run -p 8000:8000 -v $(pwd)/app:/app starlette
```

Then access [`localhost:8000`](http://localhost:8000).

## References
* [tiangolo's GitHub repo](https://github.com/tiangolo/uvicorn-gunicorn-starlette-docker)
* [uvicorn-gunicorn-docker](https://github.com/tiangolo/uvicorn-gunicorn-docker/tree/master/docker-images)