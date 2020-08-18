import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'attendeesPage.dart';
import 'event.dart';
import 'group.dart';
import 'package:http/http.dart' as http;
import 'member.dart';
class eventInfoPage extends StatefulWidget {
  event Event;
  group groupObj;
  String curruser;
  eventInfoPage({Key key, this.Event, this.groupObj, this.curruser}) : super(key: key);
  @override
  _eventInfoPageState createState() => _eventInfoPageState();
}


class _eventInfoPageState extends State<eventInfoPage> {
  List<String> attendees() {
    List<String> a = new List();
    for (member m in widget.Event.attendees) {
     a.add(m.username);
    }
    return a;
  }
  @override
  Widget build(BuildContext context) {

    TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
    MaterialButton RSVPBtn = MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      height: 50,
      padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
      color: Color(0xff7ddcff),
      child: Text(
        "RSVP",
        textAlign: TextAlign.center,
        style: style.copyWith(fontFamily: 'Molengo'), //,
        //fontWeight: FontWeight.bold),
      ),
      textColor: Color(0xff535a70),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
      ),
      onPressed: () {
        RSVPtoServer();
      },
    );
    return Scaffold(
        appBar: AppBar(
          leading: IconButton(
            icon: Icon(Icons.arrow_back_ios),
            color: Color(0xff1e6b87),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
          actions: <Widget>[
            IconButton(
              icon: Icon(Icons.people),
              color: Color(0xff1e6b87),
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) => attendeesPage(e: widget.Event, g: widget.groupObj)));
              },
            ),
          ],
          backgroundColor: Colors.white,
          title: Text(widget.Event.name,
              style: TextStyle(
                  color: Color(0xff1e6b87),
                  fontFamily: "Molengo",
                  fontSize: 30)),
          elevation: 0,
        ),
        body: Stack(
          children: <Widget>[
            Positioned(
              width: MediaQuery.of(context).size.width / 5 * 4,
              left: MediaQuery.of(context).size.width / 10,
              top: MediaQuery.of(context).size.height / 5,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  Icon(widget.Event.recurring?Icons.event_available:Icons.event,
                      color: Color(0xff535a70),
                      size: MediaQuery.of(context).size.width / 5),
                  Text(widget.Event.name,
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 20)),
                  SizedBox(height:10),
                  Text("Date: " + widget.Event.dateTime.toIso8601String().substring(0, 10),
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 25)),
                  SizedBox(height:10),
                  Text("Time: " + widget.Event.dateTime.toIso8601String().substring(11, 16),
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 25)),
                  SizedBox(height:10),
                  Text("Duration: " + widget.Event.duration.toString() + ((widget.Event.duration == 1) ? " hour" : " hours"),
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 25)),
                  SizedBox(height:10),
                  Text(widget.Event.recurring ? "This event happens weekly" : "",
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 25)),
                  SizedBox(height:10),
                  (!attendees().contains(widget.curruser)) ? RSVPBtn : Container()
//                  Text(widget.doc.uploadDate,
//                      style: TextStyle(
//                          color: Color(0xff535a70),
//                          fontSize: MediaQuery.of(context).size.width / 25)),
//                  Text("Owner: " + widget.doc.uploader,
//                      style: TextStyle(
//                          color: Color(0xff535a70),
//                          fontSize: MediaQuery.of(context).size.width / 20)),

                ],
              ),
            )
          ],
        )

    );
  }
  createAlertDialog(BuildContext context, String message) {
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text(message,
                style:
                TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK"),
                  onPressed: () {
                    Navigator.of(context).pop();
                  })
            ],
          );
        });
  }

  void RSVPtoServer() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    var r;
    try {
      r = await http.post('http://134.122.21.105:8080/events/' + widget.Event.id.toString() + '/RSVP',
        headers:{
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "Bearer " + authT
        },
      );
    } catch (e) {
      createAlertDialog(context, "Could not connect to server, try again later!");
    }
    if (r.statusCode == 200) {
      Navigator.pop(context);
      createAlertDialog(context, "Successfully RSVP'd to " + widget.Event.name);
    } else {
      createAlertDialog(context, "Couldn't RSVP to " + widget.Event.name + "!");
    }
  }
}

