import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:conglomerate_ui/createEventPage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

import 'event.dart';
import 'eventInfoPage.dart';
import 'group.dart';
import 'member.dart';

class eventsPage extends StatefulWidget {
  final group groupObj;
  eventsPage({Key key, this.groupObj}) : super(key: key);
  @override
  _eventsPageState createState() => _eventsPageState();
}

class _eventsPageState extends State<eventsPage> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 30.0);
  String curruser;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          leading: IconButton(
            icon: Icon(Icons.arrow_back_ios),
            color: Color(0xff1e6b87),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
          backgroundColor: Colors.white,
          title: Text("Events", style: TextStyle(
              color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30)),
          elevation: 0,
          actions: <Widget>[
            IconButton(
                color: Color(0xff1e6b87),
                icon: const Icon(Icons.add, size: 30),
                onPressed: () {
                  Navigator.push(context, MaterialPageRoute(
                      builder: (context) => createEventPage(groupObj: widget.groupObj)));
                }
            )
          ],
        ),
        body: FutureBuilder(
            future: create_arr(context),
            builder: (BuildContext context, AsyncSnapshot snapshot) {
              switch (snapshot.connectionState) {
                case ConnectionState.none:
                case ConnectionState.waiting:
                  return Center(child: SizedBox(height: 100,
                      width: 100,
                      child: new CircularProgressIndicator()));
                  break;
                default:
                  if (snapshot.hasError) {
                    // print('here\n');
                    return Container();
                  } else {
                    //print("length");
                    //print(snapshot.data.length);
                    return _buildListView(snapshot.data);
                  }
              }
            }
        )
    );
  }

  Future<List<event>> fetchEvents() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    var r = await http.get('http://134.122.21.105:8080/events/' + widget.groupObj.id.toString() + '/list-events',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
    );
//    print("fetchevents stat: " + r.statusCode.toString());

    var events = List<event>();
    if (r.statusCode == 200) {
      var eventsJson = json.decode(r.body);
      for (var e in eventsJson) {
//        print(e);
        events.add(event.fromJson(e));
      }
    }
//    print("events size: " + events.length.toString());
    return events;

  }
  Future<List> create_arr(BuildContext context) async {
//    print("create arr");
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.getString('username');
    //TODO: create array of events
    List<Widget> widgets = new List();
    widgets.add(SizedBox(height: 5));
    //GroupList groupList = await getGroupNames();
    curruser = prefs.getString('username');
    //List<group> groupList = await fetchG();
    //fetch events
//    List<event> eventList = new List();
    List<event> eventList = await fetchEvents();
//    print("eventslist: " + eventList.length.toString());
    List<member> mem = new List();
    //dummy events
//    mem.add(new member(username:"Vanshika", id:0));
//    mem.add(new member(username:"Shree", id:1));
//    mem.add(new member(username:"agontijo", id:1));
//    event e1 = new event(name:"Dummy event1", attendees:mem, duration:1, dateTime:DateTime.now(),recurring:true, id:0);
//    event e2 = new event(name:"Dummy event2", attendees:mem, duration:2, dateTime:DateTime.now(),recurring:false, id:0);
//    eventList.add(e1);
//    eventList.add(e2);
    for (final e in eventList) {
      widgets.add(
          Card(child: ListTile(
            leading: e.recurring? Icon(Icons.event_available, color: Color(0xff1e6b87),) : Icon(Icons.event, color: Color(0xff1e6b87),),
            title: Text(e.name, style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo")),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => eventInfoPage(Event: e, groupObj: widget.groupObj, curruser: curruser)));
            },
          )
          ));

    }
    widgets.add(Container(child: ListTile(
      title: Center(child: Text("There are " + eventList.length.toString() +
          " events scheduled",
          style: TextStyle(color: Color(0xff535a70), fontFamily: "Molengo"))),
    )
    ));

    return widgets;
  }

  Widget _buildListView(List list) {
    return Column(
      children: <Widget>[
        Expanded(
          child: ListView(
            shrinkWrap: true,
            children: list,
          ),
        ),
      ],
    );
  }
}