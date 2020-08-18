import 'dart:convert';

import 'package:conglomerate_ui/member.dart';
import 'package:conglomerate_ui/UserProfile.dart';
import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'package:conglomerate_ui/group.dart';

import 'event.dart';


class attendeesPage extends StatefulWidget{
  final event e;
  final group g;
  attendeesPage({Key key, this.e, this.g}): super(key: key);
  //_userHomePageState createState() => _userHomePageState();
  State<StatefulWidget> createState() {
    return _attendeesPage();
  }
}



class _attendeesPage extends State<attendeesPage> {
  List attendeesList;
  Widget attendeeTiles;
  List<String> RSVP () {
    List<String> attendees = new List();
//    if (widget.e.attendees == null) {
//      createAlertDialog(context, "attendees null");
//    }
//    else {
      for (member m in widget.e.attendees) {
        attendees.add(m.username);
      }
//    }
    return attendees;
  }

  List<String> notRSVP() {
    List<String> attendees = RSVP();
    List<String> notAttendees = new List();
//    if (attendees == null) {
//      createAlertDialog(context, "attendees null again");
//    }
//    else {
      for (member m in widget.g.members) {
        if (!attendees.contains(m.username)) {
          notAttendees.add(m.username);
        }
      }
//    }
    return notAttendees;
  }

  List create_arr(BuildContext context) {

    List<Widget> widgets = new List();
    widgets.add(SizedBox(height: 5));
    //GroupList groupList = await getGroupNames();
//    List<member> attendeeList = widget.e.attendees;
//    if (RSVP() == null || notRSVP() == null) {
//      createAlertDialog(context, "RSVP or notRSVP null");
//    } else {
      for (final m in RSVP()) {
        widgets.add(
            Card(child: ListTile(
              title: Text(m, style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo")),
            )
            ));
      }
      for (final m in notRSVP()) {
        widgets.add(
            Card(color: Colors.white30,
                  child: ListTile(
              title: Text(m, style: TextStyle(color: Color(0xff535a70), fontFamily: "Molengo")),
            )
            ));
      }
//    }

    return widgets;
  }





  void _buildListView() {
    attendeesList = create_arr(context);
    attendeeTiles = Column(
      children: <Widget>[
        Expanded(
          child: ListView(
            shrinkWrap: true,
            children: attendeesList,
          ),
        ),
      ],
    );
  }


  Widget build(BuildContext context) {
    final userHomeAppbar = AppBar(
      leading: IconButton(
        icon: Icon(Icons.arrow_back_ios),
        color: Color(0xff1e6b87),
        onPressed: () {
          Navigator.pop(context);
        },
      ),
      backgroundColor: Colors.white,
      title: Text("Attendees", style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30)),
      elevation: 0,
    );

    _buildListView();

    return Scaffold(
        appBar: userHomeAppbar,
        body: attendeeTiles
    );
  }

}
