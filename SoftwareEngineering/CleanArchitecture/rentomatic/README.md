# Rentomatic

The code in this repository is originally from Giordani's book, [Clean Architectures in Python, First Edition](https://www.pycabook.com/). The GitHub repo for the original code can be found [here](https://github.com/pycabook/rentomatic).

To install dependencies, run:
```shell
pip install -r requirements.txt
```

## Unit Test
To test, run:
```shell
pip install -e .
```

Then:
```
pytest
```
See [here for more info](https://docs.pytest.org/en/6.2.x/goodpractices.html) 

## Integration Test
You need a Docker installed to run the integration test. 
Make sure you don't have another postgres server on your computer.
Run:

```shell
pytest --integration
```


## Server
To run a development server, run:
```shell
flask run
```