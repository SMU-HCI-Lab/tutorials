# state.py

import logging
import sys

from event import Event, PinEntered, DeviceLocked

logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)


class State(object):

    def __init__(self):
        logging.info("Moved to a new state: {}".format(str(self)))

    def next(self, input):
        pass

    def run(self) -> None:
        logging.info("Current State: {}".format(str(self)))

    def __repr__(self):
        return self.__str__()
    
    def __str__(self):
        return self.__class__.__name__
    

class LockedState(State):
    def next(self, event: Event) -> State:
        if type(event) is PinEntered:
            return UnlockedState()
        else:
            logging.info("No transition")
            return self


class UnlockedState(State):
    def next(self, event: Event) -> State:
        if type(event) is DeviceLocked:
            return LockedState()
        else:
            logging.info("No transition")
            return self
