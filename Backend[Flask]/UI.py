from tkinter import Frame,Button,Label,Canvas,Tk,Text
from tkinter.filedialog import askopenfilename
from PIL import Image,ImageTk
import requests




class App(object):
    
    
    def importImageFile(self):
        self.imageCanvas.delete('all')
        path = askopenfilename(filetypes = [('All Images',['*.jpeg','*.jpg','*.png'])])
        if path:
            self.path = path
            img = Image.open(path)
            img = img.resize((650, 400), Image.ANTIALIAS)
            self.imageCanvas.image = ImageTk.PhotoImage(img)
            self.imageCanvas.create_image(400,200,anchor = 'center',image=self.imageCanvas.image)
        
    
    def display(self, frame):
        self.imageCanvas.image = ImageTk.PhotoImage(image = Image.fromarray(frame))
        self.imageCanvas.create_image(400,200,anchor = 'center',image = self.imageCanvas.image)
    
    def sendImageServer(self):
        if(self.path != ""):
            sample_file = open(self.path, "rb")
            upload_file = {"Uploaded file": sample_file}
            sub = self.txt.get("1.0", "end-1c")
            url = "http://192.168.1.8:5000/mark_a?subject="+sub
            r = requests.post(url, files = upload_file) 
        
   
    def __init__(self):
        self.imageCanvas = None
        self.path = ""
        self.root = Tk()
        self.root.title('Attendance Management')
        
        # Canvas Frame
        self.mainFrame = Frame(self.root)
        self.mainFrame.pack()
        self.imageCanvas = Canvas(self.mainFrame,width = 800,height = 400,highlightthickness=1,highlightbackground="black")
        self.imageCanvas.pack(pady = 5)
        
        #Button Frame
        self.buttonFrame = Frame(self.root)
        self.buttonFrame.pack(pady = 10)
        
        # Import Button
        self.importImageButton = Button(self.mainFrame,text = 'Import Image',command=self.importImageFile)
        self.importImageButton.pack(pady = 5)
        
        self.inputtxt = Label(self.buttonFrame,text = "Subject Name", height = 1, width = 10) 
        self.inputtxt.grid(row = 0, column = 1, pady = 5)
        
        self.txt = Text(self.buttonFrame, height = 1, width = 10) 
        self.txt.grid(row = 0, column = 2, padx= 5, pady = 5)
        
       
        
        self.submit = Button(self.buttonFrame,text = 'Submit',command = self.sendImageServer)
        self.submit.grid(row = 0, column = 3,padx = 5, pady = 5)
        
        
        self.root.mainloop()
    
        
if __name__=='__main__':
    App()