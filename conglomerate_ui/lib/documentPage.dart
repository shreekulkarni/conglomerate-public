import 'dart:convert';

//import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/folderContent.dart';

import 'package:documents_picker/documents_picker.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:conglomerate_ui/group.dart';

import 'dart:async';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';



import 'DocumentInfoPage.dart';
import 'documentSearchPage.dart';
import 'fileModel.dart';
import 'documents.dart';
import 'folderModel.dart';



class documentPage extends StatefulWidget {
  @override
  final group groupObj;
  documentPage({Key key, @required this.groupObj}): super(key: key);
  _documentPageState createState() => _documentPageState();
}

class _documentPageState extends State<documentPage> {
  String curruser;
  bool _share = false;
  String filePath = "";
  List<fileModel> files = new List();
  List<folderModel> folders = new List();
  File file;
  List<documents> allDocs = new List();
  final folder_name_controller = TextEditingController();
  TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));

  String getName() {
    return widget.groupObj.name;
  }
  //create a dialog to prompt the user for a folder name
  creatFolderDialog(BuildContext context) {
    TextField folderNameField = TextField(
        obscureText: false,
        style: alertstyle,
        controller: folder_name_controller,
        decoration: InputDecoration(
            hintText: "Folder Name"
        )
    );

    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            content: Card(
                color: Colors.transparent,
                elevation: 0.0,
                child: Column(
                  children: <Widget>[
                    folderNameField
                  ],
                )
            ),
            actions: <Widget>[
              MaterialButton(
                child: Text("Create Folder", style: alertstyle),
                onPressed: () async {
                  if (folder_name_controller.text == "") {
                    createAlertDialog(context, "Folder name cannot be blank!");
                  } else {
                    await createFolder(folder_name_controller.text);
                    folder_name_controller.clear();
                    Navigator.pop(context);
                    setState(() {
                      _buildFuture();
                    });
                  }
                },
              )
            ],
          );
        });
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

  createFolder(String name) async {
    //TODO: create folder and add to list of folders for a group
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    //print(widget.groupObj.id.toString());

    http.Response r = await http.post(
        'http://134.122.21.105:8080/folders/' + widget.groupObj.id.toString() +
            '/create',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        },
        body: name
    );
    print(r.statusCode);
    if (r.statusCode == 201) {
      Navigator.pop(context);
      createAlertDialog(context, "Folder created successfully!");
    }
    else {
      createAlertDialog(context, "Couldn't create folder!");
    }
  }

  createFolderWidget(folderModel fold) {
    String name = fold.folderName;
    if (name.length > 8) {
      name = name.substring(0, 8) + " ...";
    }
    return new Column(
      children: <Widget>[
        IconButton(
          icon: Icon(Icons.folder),
          color: Color(0xff1e6b87),
          iconSize: MediaQuery.of(context).size.width / 5,
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => folderContent(groupObj: widget.groupObj, f: fold, curruser: curruser)),
            );
          },
        ),



        Text(name, style: TextStyle(fontFamily: alertstyle.fontFamily, color: alertstyle.color, fontSize: MediaQuery.of(context).size.width / 30))
      ],
    );
  }

  createFileWidget(fileModel file) {
    String name = file.fileName;
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
//            print(widget.username);
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => DocumentInfoPage(doc: file, curruser: curruser)),
            );
          },
        ),
        Text(name, style: TextStyle(fontFamily: alertstyle.fontFamily, color: alertstyle.color, fontSize: MediaQuery.of(context).size.width / 30))
      ],
    );
  }
  //TODO: delete when actual folder/file getting has been implemented
  //for display purposes and sample code only

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
    var uri = Uri.parse('http://134.122.21.105:8080/documents/' + getId().toString() + '/upload');
    var request = http.MultipartRequest('POST', uri)
      ..headers['authorization'] = "bearer " + authT
      ..fields['groupingID'] = getId().toString()
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
      createAlertDialog(context, "File uploaded successfully! "
          "Refresh to view.");
    }
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

  _buildFuture() {
    //Timer.periodic(Duration(seconds: 10), (timer) {
    return FutureBuilder(
        future: fetchDocuments(context),
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
                return Container();
              } else {
                return buildGridView(snapshot.data);
              }
          }
        }
    );
    //});
  }

  Future<List<Widget>> fetchDocuments(BuildContext context) async {
    List<Widget> list = new List();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    curruser = prefs.getString('username');

    var r = await http.get('http://134.122.21.105:8080/documents/' + getId().toString() + '/list-docs',

      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
    );

    if (r.statusCode == 200) {
      var docsJson = json.decode(r.body) as List;
      for (var d in docsJson) {

        widget.groupObj.docs = documents.fromJson(d);
        files = widget.groupObj.docs.files;
        folders = widget.groupObj.docs.folders;
      }
      for (var f in folders) {
        //print(f.folderName);
        list.add(createFolderWidget(f));
      }
      for (var f in files) {
        list.add(createFileWidget(f));
      }
    }
    return list;
  }

  Widget buildGridView(List list) {
    return GridView.count(
        crossAxisCount: 3,
        children: list
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title:Text(getName(), style: TextStyle(color: Color(0xff1e6b87), fontSize: 25.0, fontFamily: "Molengo")),
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
                  MaterialPageRoute(builder: (context) => documentSearchPage(groupObj:widget.groupObj)),
                );
              }
          ),
          IconButton(icon: Icon(Icons.create_new_folder, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                creatFolderDialog(context);
              }
          ),
          IconButton(
              icon: Icon(Icons.file_upload, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                shareFile();

              }
          ),
          IconButton(icon: Icon(Icons.refresh, size: 30),
              color: Color(0xff1e6b87),
              onPressed: () {
                setState(() {
                  _buildFuture();
                });
              }
          ),
        ],
      ),
      body: _buildFuture(),
    );
  }
}
