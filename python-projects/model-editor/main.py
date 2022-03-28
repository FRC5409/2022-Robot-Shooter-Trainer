import sys

from PyQt5 import QtWidgets

from app import ModelEditorApplication
from options import ApplicationOptions

import utility 

if __name__ == '__main__':
    app = QtWidgets.QApplication([])
    
    ex = ModelEditorApplication( 
        ApplicationOptions(utility.parse(sys.argv[1:])))

    sys.exit(app.exec_())