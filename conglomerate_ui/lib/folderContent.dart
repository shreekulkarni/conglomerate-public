import 'dart:convert';

import 'package:conglomerate_ui/createGroupPage.dart';
//import 'package:dio/dio.dart';

import 'package:documents_picker/documents_picker.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:conglomerate_ui/group.dart';

import 'dart:async';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:path_provider/path_provider.dart';
//import 'package:flutter_downloader/flutter_downloader.dart';

import 'DocumentInfoPage.dart';
import 'documentSearchPage.dart';
import 'fileModel.dart';
import 'documents.dart';
import 'folderModel.dart';



class folderContent extends StatefulWidget {
  @override
  final group groupObj;
  folderModel f;
  String curruser;
  folderContent({Key key, @required this.groupObj, this.curruser, @required this.f}): super(key: key);
  _folderContentState createState() => _folderContentState();
}

class _folderContentState extends State<folderContent> {
  bool _share = false;
  String filePath = "";
  File file;
  final folder_name_controller = TextEditingController();
  TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));

  String getName() {
    return widget.groupObj.name;
  }

  createFileWidget(fileModel fileM) {
    String name = fileM.fileName;
    if (name.length > 8) {
      name = name.substring(0, 8) + " ...";
    }
    return new Column(
      children: <Widget>[
        IconButton(
          icon: Icon(Icons.insert_drive_file),
          color: Color(0xff535a70),
          iconSize: MediaQuery.of(context).size.width / 5,
          onPressed: () {
//
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => DocumentInfoPage(doc: fileM, curruser: widget.curruser,)),
            );
          },
        ),
        Text(name, style: TextStyle(fontFamily: alertstyle.fontFamily, color: alertstyle.color, fontSize: MediaQuery.of(context).size.width / 30))
      ],
    );
  }

  Future<void> shareFile() async {
    List<dynamic> docs = await DocumentsPicker.pickDocuments;
    if (docs == null || docs.isEmpty) return null;

    filePath = docs[0] as String;

    if (filePath == null) return;
    file = new File(filePath);
    await createChoiceDialog(context,
        "Would you like to make this document visible to other group members?");
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    var uri = Uri.parse('http://134.122.21.105:8080/documents/' + widget.groupObj.id.toString() +'/'+ widget.f.id.toString() + '/upload');
    var request = http.MultipartRequest('POST', uri)
      ..headers['authorization'] = "bearer " + authT
      ..fields['groupingID'] = widget.groupObj.id.toString()
      ..fields['shared'] = _share.toString()
      ..files.add(await http.MultipartFile.fromPath(

        'file', filePath,
      ));
    var r = await request.send();
    if (r.statusCode != 201) {
      createAlertDialog(context, "There were problems uploading your file. "
          "Try again later");
    }
    else {
      //Navigator.pop(context);

      createAlertDialog(context, "File uplaoded successfully in folder \"" + widget.f.folderName + "\"!\nRefresh to view.");
    }

  }
  createAlertDialog(BuildContext context, String message) {
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text(message,
                style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK"),
                  onPressed: () {
                    Navigator.of(context).pop();
                  }
              )
            ],
          );
        });
  }

  createChoiceDialog(BuildContext context, String message) {
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text(message,
                style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
            actions: <Widget>[

              MaterialButton(
                  child: Text("Yes"),
                  onPressed: () {
                    _share = true;

                    Navigator.of(context).pop();
                  }
              ),
              MaterialButton(
                  child: Text("No"),
                  onPressed: () {
                    _share = false;
                    Navigator.of(context).pop();
                  }
              )

            ],
          );
        });
  }
  int getId() {
    return widget.groupObj.id;
  }

  List<Widget> _makeFileWidget() {
    List<Widget> l = new List();

    for (var fileM in widget.f.documents) {
      print("make file");
      //print(file);
      l.add(new Column(
        children: <Widget>[
          IconButton(
            icon: Icon(Icons.insert_drive_file),
            color: Color(0xff535a70),
            iconSize: MediaQuery.of(context).size.width / 5,
            onPressed: () {

              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => DocumentInfoPage(doc: fileM, curruser: widget.curruser)),
              );
            },
          ),
          Text((fileM.fileName.length > 8) ? (fileM.fileName.substring(0, 8) + " ...") : (fileM.fileName), style: TextStyle(fontFamily: alertstyle.fontFamily, color: alertstyle.color, fontSize: MediaQuery.of(context).size.width / 30))
        ],
      ));
    }
    return l;
  }
  void fetchDocuments(BuildContext context) async {
    //List<Widget> list = new List();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    String curruser = prefs.getString('username');


    var r = await http.get(
      'http://134.122.21.105:8080/documents/' + getId().toString() +
          '/list-docs',

      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
    );
    List<folderModel> folders = new List();
    List<fileModel> files = new List();

    if (r.statusCode == 200) {
      var docsJson = json.decode(r.body) as List;
      for (var d in docsJson) {
        widget.groupObj.docs = documents.fromJson(d);
        files = widget.groupObj.docs.files;
        folders = widget.groupObj.docs.folders;
      }
//      for (var f in folders) {
//        //print(f.folderName);
//        if (f.id == widget.f.id) {
//          Navigator.pop(context);
//          Navigator.push(
//            context,
//            MaterialPageRoute(builder: (context) => folderContent(groupObj: widget.groupObj,
//              f: f,)),
//          );
//          break;
//        }
//      }
      for (var f in folders) {
        //print(f.folderName);
        if (f.id == widget.f.id) {
//          Navigator.pop(context);
//          Navigator.push(
//            context,
//            MaterialPageRoute(builder: (context) => folderContent(groupObj: widget.groupObj, f: f, curruser: widget.curruser, authToken: widget.authToken,)),
//          );
          widget.f = f;

          break;
        }
      }
      buildGridView(_makeFileWidget());
    }
  }
  Widget buildGridView(List list) {
    return GridView.count(
        crossAxisCount: 3,
        children: list
    );
  }
  @override
  Widget build(BuildContext context) {
    fetchDocuments(context);
    return Scaffold(
      appBar: AppBar(
        title:Text(widget.f.folderName, style: TextStyle(color: Color(0xff1e6b87), fontSize: 25.0, fontFamily: "Molengo")),
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios, size: 30),
          color: Color(0xff1e6b87),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        actions: <Widget>[
          IconButton(icon: Icon(Icons.search, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => documentSearchPage()),
                );
              }
          ),
          IconButton(
              icon: Icon(Icons.file_upload, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                setState(() {
                  shareFile();
                });
              }


          ),
          IconButton(icon: Icon(Icons.refresh, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                setState(() {
                  fetchDocuments(context);
                });
              }
          ),
        ],
      ),
      body:
      buildGridView(_makeFileWidget()),

    );
  }
}
