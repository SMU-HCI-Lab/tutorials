# event.py

class Event:
    def __init__(self, message: str = None) -> None:
        self.message = message


class PinEntered(Event):
    def __init__(self, message: str = None) -> None:
        super().__init__(message=message)


class DeviceLocked(Event):
    def __init__(self, message: str = None) -> None:
        super().__init__(message=message)
