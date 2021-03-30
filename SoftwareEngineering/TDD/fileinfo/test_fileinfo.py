from fileinfo import FileInfo
from unittest.mock import patch


def test_init():
    filename = 'somefile.ext'
    fi = FileInfo(filename)
    assert fi.filename == filename


def test_init_relative():
    filename = 'somefile.ext'
    relative_path = '../{}'.format(filename)
    fi = FileInfo(relative_path)
    assert fi.filename == filename


def test_get_info():
    filename = 'somefile.ext'
    original_path = '../{}'.format(filename)

    with patch('os.path.abspath') as abspath_mock:
        test_abspath = 'some/abs/path'
        abspath_mock.return_value = test_abspath
        fi = FileInfo(original_path)
        assert fi.get_info() == (filename, original_path, test_abspath)


# @patch('os.path.abspath')
# def test_get_info_with_decorator(abspath_mock):
#     test_abspath = 'some/abs/path'
#     abspath_mock.return_value = test_abspath
#
#     filename = 'somefile.ext'
#     original_path = '../{}'.format(filename)
#
#     fi = FileInfo(original_path)
#     assert fi.get_info() == (filename, original_path, test_abspath)


@patch('os.path.getsize')
@patch('os.path.abspath')
def test_get_info(abspath_mock, getsize_mock):
    filename = 'somefile.ext'
    original_path = '../{}'.format(filename)

    test_abspath = 'some/abs/path'
    abspath_mock.return_value = test_abspath

    test_size = 1234
    getsize_mock.return_value = test_size

    fi = FileInfo(original_path)
    info = fi.get_info()

    abspath_mock.assert_called_with(original_path)
    getsize_mock.assert_called_with(original_path)
    assert info == (filename, original_path, test_abspath, test_size)
