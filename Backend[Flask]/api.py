from flask import Flask, jsonify, request
import flask
import werkzeug
from helper import check_if_face, db_verify, new_user, helper_geta, detect_faces

app = Flask(__name__)


@app.route('/post_image',  methods = ['POST'])
def post_image():
    files_ids = list(flask.request.files)
    file_id = files_ids[0]   
    imagefile = flask.request.files[file_id]
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    imagefile.save(filename)
    
    if(check_if_face(filename)):   
        return jsonify("OK")
    else:
        return jsonify("No face detected")
    
@app.route('/get_a', methods = ['GET'])
def return_atten():
    uid = request.args.get("uid")
    sub = helper_geta(uid);
        
    return jsonify(sub)
@app.route("/post_info", methods = ['POST'])
def post_info():
    data = flask.request.json
    if new_user(data, False):
        return jsonify("OK")
    else:
        return jsonify("not OK")
@app.route("/verify", methods = ['GET'])
def verify():
    username = request.args.get("username")
    password = request.args.get("password")
    
    result = db_verify(username, password)
    
    return jsonify(result)

@app.route("/mark_a", methods = ['POST'])
def mark_a():
    files_ids = list(flask.request.files)
    file_id = files_ids[0]   
    imagefile = flask.request.files[file_id]
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    imagefile.save(filename)
    sub = request.args.get("subject")
    detect_faces(sub, filename)
    return jsonify("ok")
    
    
if __name__ == "__main__":
    app.run(host = '192.168.43.165', port = 5000, threaded = False)