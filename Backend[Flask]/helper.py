import pymongo
import numpy as np
import cv2
from mtcnn import MTCNN
import os
from model import InceptionV3


general = [{"sub_name":"CN", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"TOC", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"ISEE", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"SEPM", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"DBMSL", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"CNL", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"SDL_TUT", "total_lectures":0, "attended_lectures": 0},
              {"sub_name":"SDL", "total_lectures":0, "attended_lectures": 0}]


client = pymongo.MongoClient(host = 'localhost',port = 27017)
db = client.dbms_database
print('Database Created')
#print(client.list_database_names())
model = InceptionV3()


def insert_student(s):
    
    db.students.insert_one(s)
    di = {"uid":s['uid'], 
          "subjects": general}
    
    db.subjects.insert_one(di)
    
    return True

def check_if_face(imagepath):
    image = cv2.imread(imagepath)
    
    img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    detector = MTCNN()
    
    di = detector.detect_faces(img)
    if(len(di)==0):
        os.remove(imagepath)
        return False
    else:
        di = di[0]
    
    if(di['confidence']>0.80):
        x,y,w,h = di['box']
        x-=10
        y-=10
        w+=10
        h+=10
        _slice = image[y:y+h, x:x+w,:]
        _slice = cv2.resize(_slice, (256,256))
        cv2.imwrite(imagepath[:-4]+"_1.jpg", _slice)
        return True
    else:
        os.remove(imagepath)
        return False

def db_verify(username, password):
    res = db.students.find_one({"username":username})
    if res == None:
        return "No match"
    if(res['password']==password):
        return res['uid']
    else:
        return "No match"
    
def new_user(student, remove_image):
    imagepath = student['image_path']
    student.pop('image_path', 'no key')
    img = cv2.imread(imagepath[:-4]+"_1.jpg")
    img = model.predict(img[np.newaxis, :]).flatten() # img = model.predict(img)
    img = img.tostring()
    student['face_encodings'] = img
    
    if(insert_student(student)):
        if(remove_image):
            os.remove(imagepath)
        return True

def similarities(image, images):
    image = image.flatten()
    return np.sqrt(np.mean(pow(np.array(image)-np.array(images), 2)))

def detect_faces(cur_lecture, img_path):
    img = db.students.find({}, {'face_encodings':1, '_id':0})
    faces = []
    for im in img:  
        faces.append(np.fromstring(im['face_encodings'], np.float32))
    usernames = []
    names = db.students.find({}, {'uid':1, '_id':0})
    for n in names:
        usernames.append(n['uid'])

    image = cv2.imread(img_path)
    img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    detector = MTCNN()
    
    di = detector.detect_faces(img)
    detected_faces = []
    for d in di:
        x,y,w,h = d['box']
        x-=10
        y-=10
        w+=10
        h+=10
        _slice = image[y:y+h, x:x+w,:]
        
        _slice = cv2.resize(_slice, (256,256))
        
        encodings = model.predict(_slice[np.newaxis, :])
        rms = similarities(encodings, faces)
        amax = np.argmin(rms, axis = 0)
        
        detected_faces.append(usernames[amax])
    mark_attendance(cur_lecture, list(np.unique(detected_faces)))
    
def helper_geta(uid):
    res = db.subjects.find({'uid':uid})[0]
    return res['subjects']
            
def mark_attendance(cur_lecture, detected_faces):
    res = db.subjects.find()
    
    i = None
    for j,sub in enumerate(general):
        if(sub['sub_name']==cur_lecture):
            i = j
            break;
    
    for re in res:
        curr = re['subjects']
        uid = re['uid']
        if uid in detected_faces:
            curr[i]['attended_lectures']+=1;
        curr[i]['total_lectures']+=1;
        
        db.subjects.update_one({'uid':uid}, {"$set":{"subjects":curr}})






