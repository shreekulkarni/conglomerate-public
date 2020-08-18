import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

import 'group.dart';

class createEventPage extends StatefulWidget {
  final group groupObj;
  createEventPage({Key key, @required this.groupObj}): super(key: key);
  @override
  _createEventPageState createState() => _createEventPageState();
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

class _createEventPageState extends State<createEventPage> {
  static TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  static final event_name_controller = TextEditingController();
  String dropdownValue = '1 hour';
  DateFormat df = DateFormat('MM/dd/yyyy hh:mm');

  //TODO: default date and time is from algorithm
//  DateTime start = DateTime.now();
  DateTime selectedDate = DateTime.now().add(Duration(days:1));
  static bool checkVal = false;

  final eventnameField = TextField(
    obscureText: false,
    style: style,
    controller: event_name_controller,
    decoration: InputDecoration(
        contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        hintText: "Event Name",
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
  );

//  Future<Null> _selectDate(BuildContext context) async {
//    final DateTime picked = await showDatePicker(
//        context: context,
//        firstDate: DateTime.now().add(Duration(days: 1)),
//        initialDate: DateTime.now().add(Duration(days: 1)),
//        lastDate: DateTime.now().add(Duration(days: 11)));
//    if (picked != null && picked != selectedDate)
//      setState(() {
//        selectedDate = picked;
//      });
//  }

  void changeCheck(bool checked) {
    setState(() {
      checkVal = checked;
    });
  }

  DropdownButton<String> durationBtn () {

    return DropdownButton<String>(
      value: dropdownValue,
      icon: Icon(Icons.keyboard_arrow_down),
      iconSize: 24,
      elevation: 16,
      style: TextStyle(color: Color(0xff535a70), fontFamily: "Molengo", fontSize: style.fontSize),
      underline: Container(
        height: 2,
      ),
      onChanged: (String newValue) {
        setState(() {
          dropdownValue = newValue;
        });
      },
      items: <String>['1 hour', '2 hours', '3 hours', ]
          .map<DropdownMenuItem<String>>((String value) {
        return DropdownMenuItem<String>(
          value: value,
          child: Text(value),
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
//    event_name_controller.clear();
    final bottom = MediaQuery.of(context).viewInsets.bottom;

    final dateField = TextField(
      obscureText: false,
      style: style,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: df.format(selectedDate),
//              (selectedDate == null) ? "Date" : selectedDate.toIso8601String(),
          border:
              OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
      onTap: () {
        showCupertinoModalPopup<void>(
            context: context,
            builder: (BuildContext context) {
              return _buildBottomPicker(
                  CupertinoDatePicker(
                      mode: CupertinoDatePickerMode.dateAndTime,
                      minimumDate:  DateTime.now().add(Duration(days: 0)),
                      initialDateTime: DateTime.now().add(Duration(days: 0)),
                      maximumDate: DateTime.now().add(Duration(days: 10)),
                      onDateTimeChanged: (date) {
                        setState(() {
                          selectedDate = date;
                        });
                      }),
                  MediaQuery.of(context).size.height);
            });
      },
    );


    CheckboxListTile recurring = CheckboxListTile(
      title: Text("Recurring",
          style: TextStyle(
              fontFamily: "Molengo",
              color: Color(0xff535a70),
              fontSize: dateField.style.fontSize)),
      value: checkVal,
      activeColor: Color(0xff535a70),
      controlAffinity: ListTileControlAffinity.leading,
      onChanged: (newVal) {
        changeCheck(newVal);
      },
    );

    int getId() {
      return widget.groupObj.id;
    }

    MaterialButton createEventbtn = MaterialButton(
      child: Text(
        "Create",
        textAlign: TextAlign.center,
        style: style.copyWith(fontFamily: 'Molengo'), //,
        //fontWeight: FontWeight.bold),
      ),
      textColor: Color(0xff535a70),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
      ),
      //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
      color: Color(0xff7ddcff),
      minWidth: MediaQuery.of(context).size.width,
      onPressed: () async {

        //TODO: upload event
        if (event_name_controller.text == "" || event_name_controller.text == null){
          createAlertDialog(context, "Event name cannot be empty!");
        } else {
          SharedPreferences prefs = await SharedPreferences.getInstance();
          String authT = prefs.getString('authToken');
          print(authT);
//          print(checkVal.toString());
          String date = selectedDate.toLocal().toIso8601String();
          String recurr = checkVal.toString();
          String dur = dropdownValue.substring(0,1);
          String event_name = event_name_controller.text;
          print(date);
          print(recurr);
          print(dur);
          print(event_name);
          var r;
          try {
            r = await http.post('http://134.122.21.105:8080/events/' + getId().toString() + '/create-event',
                          headers:{
                            "Accept": "application/json",
                            "Content-type": "application/json",
                            "authorization": "Bearer " + authT
                          },
                          body: jsonEncode(<String, String>{
                            "dateTime": date,
                            "duration": dur,
                            "eventName": event_name,
                            "recurring": recurr
                          })
                      );
          } catch (e) {
            createAlertDialog(context, "Could not connect to server, try again later!");
          }
          //print(selectedDate.toLocal().toIso8601String());
          print(r.statusCode);
          if (r.statusCode != 201) {
            createAlertDialog(context, "There were problems creating your event. "
                "Try again later");
          }
          else {
            event_name_controller.clear();
            Navigator.pop(context);
            createAlertDialog(context, "Event created successfully!");
          }
        }

      },
    );

    return Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.white,
          elevation: 0,
          leading: IconButton(
            icon: Icon(Icons.arrow_back_ios),
            color: Color(0xff1e6b87),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
          title: Text("Create Event",
              style: TextStyle(
                  color: Color(0xff1e6b87),
                  fontFamily: "Molengo",
                  fontSize: 30)),
        ),
        body: SingleChildScrollView(
            reverse: true,
            child: Padding(
                padding: EdgeInsets.only(bottom: bottom, top: 30.0, right: 30.0, left: 30.0),
                child: Column(
//                  crossAxisAlignment: CrossAxisAlignment.center,
//                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    SizedBox(height: 15),
                    eventnameField,
                    SizedBox(height: 15),
                    dateField,
                    SizedBox(height: 15),
                    Row(
                        children: <Widget>[Text("Duration",
                            style: TextStyle(
                                fontFamily: "Molengo",
                                color: Color(0xff535a70),
                                fontSize: dateField.style.fontSize)),SizedBox(width: 7,), durationBtn()],
                    ),
                    recurring,
                    SizedBox(height: 15),
                    createEventbtn
                  ],
                ))));
  }

  _buildBottomPicker(Widget picker, double height) {
    return Container(
      height: height / 3,
      padding: const EdgeInsets.only(top: 6.0),
      color: CupertinoColors.white,
      child: DefaultTextStyle(
        style: const TextStyle(
          color: CupertinoColors.black,
          fontSize: 22.0,
        ),
        child: GestureDetector(
          // Blocks taps from propagating to the modal sheet and popping.
          onTap: () {},
          child: SafeArea(
            top: false,
            child: picker,
          ),
        ),
      ),
    );
  }
}
