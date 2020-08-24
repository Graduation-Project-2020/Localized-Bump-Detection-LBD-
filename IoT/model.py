from flask import Flask					
from flask_sqlalchemy import SQLAlchemy 		#import models from flask libaray
from flask import render_template

app= Flask(__name__)    				#create object app
app.config['SQLALCHEMY_TRACK_MODIFICATIONS']= False
app.config["SQLALCHEMY_DATABASE_URI"]='postgresql #SQLengine://nzbonaujjakbhk #username:47c296bba3985a945ccace9da068cac0ec20828dfeda2f4218831d45a97f55f2@ec2-3-214-4-151.compute-1.amazonaws.com:5432 #port/d1h3s0nn45pfbd #password'					#connecting to your database 
db = SQLAlchemy(app)  

class locations(db.Model):  #Create table in your DB

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    latitude = db.Column(db.String(50), unique=False, nullable=False)
    longitude = db.Column(db.String(100), unique=False, nullable=False)

    def __init__(self , latitude, longitude):
        self.latitude = latitude
        self.longitude = longitude
        
if __name__ =='__main__':
    db.create_all()			#Run Code To Creat Your Table
