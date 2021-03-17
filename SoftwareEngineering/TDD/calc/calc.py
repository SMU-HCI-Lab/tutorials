# calc/calc.py
from functools import reduce


class Calc:
    def add(self, *args):
        return sum(args)

    def sub(self, a, b):
        return a - b

    def mul(self, *args):
        if not all(args):
            raise ValueError
        return reduce(lambda x, y: x * y, args)

    def div(self, a, b):
        if b == 0:
            return "inf"
        return a / b

    def avg(self, it, lt=None, ut=None):
        if len(it) == 0:
            return 0

        if ut is None:
            ut = max(it)

        if lt is None:
            lt = min(it)

        _it = [x for x in it if lt <= x <= ut]

        if len(_it) == 0:
            return 0

        return sum(_it) / len(_it)

