# state_machine.py
from state import LockedState
from event import Event, PinEntered, DeviceLocked


class StateMachine(object):

    def __init__(self) -> None:
        self.state = LockedState()
    
    def next(self, event: Event) -> None:
        self.state = self.state.next(event)
    
    def run(self) -> None:
        self.state.run()


if __name__ == "__main__":
    sm = StateMachine()
    sm.run()
    sm.next(PinEntered())
    sm.run()
    sm.next(DeviceLocked())
    sm.next(DeviceLocked())
