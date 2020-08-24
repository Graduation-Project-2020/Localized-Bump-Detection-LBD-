from flask import Flask
from flask_sqlalchemy import SQLAlchemy 
from flask import render_template

app= Flask(__name__)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS']= False
app.config["SQLALCHEMY_DATABASE_URI"]='postgresql://nzbonaujjakbhk:47c296bba3985a945ccace9da068cac0ec20828dfeda2f4218831d45a97f55f2@ec2-3-214-4-151.compute-1.amazonaws.com:5432/d1h3s0nn45pfbd'
db = SQLAlchemy(app)

class locations(db.Model):

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    latitude = db.Column(db.String(50), unique=True, nullable=False)
    longitude = db.Column(db.String(50), unique=True, nullable=False)

    def __init__(self , latitude, longitude):
        self.latitude = latitude
        self.longitude = longitude
        


@app.route('/')
def home():				#Create Route
    return ("Welcome To Our Page")		#Writting into Your webpage
@app.route('/list_all_data')
def ListAllData():
    location= locations.query.all()
    return render_template('list_all_data.html', mylocations=location)



if __name__ =='__main__':
   
    app.run()


