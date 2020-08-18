//import 'dart:io';
//
//import 'package:conglomerate_ui/createGroupPage.dart';
//import 'package:conglomerate_ui/documents.dart';
//import 'package:conglomerate_ui/fileModel.dart';
//import 'package:flutter/material.dart';
//import 'package:flutter_share/flutter_share.dart';
//import 'package:path_provider/path_provider.dart';
//import 'package:shared_preferences/shared_preferences.dart';
//
//
//class DocumentInfoPage extends StatefulWidget {
//  @override
//  final fileModel doc;
//  String curruser;
//
//  DocumentInfoPage({Key key, this.doc, this.curruser}) : super (key: key);
//
//  _DocumentInfoPageState createState() => _DocumentInfoPageState();
//}
//
//class _DocumentInfoPageState extends State<DocumentInfoPage> {
//  String currUserName;
//  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
//
//  fetchCurrUser() async {
//    SharedPreferences prefs = await SharedPreferences.getInstance();
//    currUserName = prefs.getString('username');
//  }
//
//  String getCurrUser() {
//    fetchCurrUser();
//    return currUserName;
//  }
//
//
//  Future<void> downloadFile() async {
//    //TODO: download the file implementation
//    HttpClient client = new HttpClient();
//    var _downloadData = List<int>();
//    var downloadPath = await getApplicationDocumentsDirectory();
//    var fileSave = new File(downloadPath.path + "/" + widget.doc.fileName);
//    client.getUrl(Uri.parse(widget.doc.documentLink))
//        .then((HttpClientRequest request) {
//      return request.close();
//    })
//        .then((HttpClientResponse response) {
//      response.listen((d) => _downloadData.addAll(d),
//          onDone: () {
//            fileSave.writeAsBytes(_downloadData);
//          }
//      );
//    });
//
//    var shared = await FlutterShare.shareFile(


import 'dart:convert';
////      title: 'Example share',
////      text: 'Example share text',
//      filePath:  fileSave.path,
//    );
//    if (shared != null) {
////      createAlertDialog(context, "File downloaded");
//    } else {
//      createAlertDialog(context, "There was a problem downloading your file");
//    }
//
//  }
//
//
//
//  @override
//  Widget build(BuildContext context) {
//    MaterialButton downloadFileBtn = MaterialButton(
//      minWidth: MediaQuery.of(context).size.width - 50,
//      height: 50,
//      padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
//      color: Color(0xff7ddcff),
//        child: Text("Download File",
//          textAlign: TextAlign.center,
//          style: style.copyWith(
//              fontFamily: 'Molengo'),//,
//          //fontWeight: FontWeight.bold),
//        ),
//        textColor: Color(0xff535a70),
//        elevation: 2,
//        shape: RoundedRectangleBorder(
//          borderRadius: BorderRadius.circular(10),
//        ),
//      onPressed: () {
//        downloadFile();
//      },
//    );
//
//    return Scaffold(
//        appBar: AppBar(
//            leading: IconButton(
//              icon: Icon(Icons.arrow_back_ios),
//              color: Color(0xff1e6b87),
//              onPressed: () {
//                Navigator.pop(context);
//              },
//            ),
//            backgroundColor: Colors.white,
//            title: Text("Document Information", style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30)),
//            elevation: 0,
//        ),
//        body: Column(
//          children: <Widget>[
//            Icon(Icons.insert_drive_file, color: Color(0xff535a70), size: MediaQuery.of(context).size.width / 5),
//            Text(widget.doc.fileName, style: TextStyle(color: Color(0xff535a70), fontSize: MediaQuery.of(context).size.width / 20)),
//            //Text(widget.doc., style: TextStyle(color: Color(0xff535a70), fontSize: MediaQuery.of(context).size.width / 25)),
//            Text("Owner: " + widget.doc.uploader, style: TextStyle(color: Color(0xff535a70), fontSize: MediaQuery.of(context).size.width / 20)),
////            (widget.doc.uploader == getCurrUser()) ?
////            Switch(
////              value: widget.doc.,
////              onChanged: (value) {
////                //TODO: send changed privacy value to server
////                setState(() {
////                  widget.doc.privacy = value;
////                });
////              },
////              activeTrackColor: Color(0xff7ddcff),
////              activeColor: Color(0xff1e6b87),
////            ) : SizedBox(height:15),
//            downloadFileBtn
//          ],
//        )
//    );
//  }
//}
import 'dart:io';
import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/fileModel.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_share/flutter_share.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

class DocumentInfoPage extends StatefulWidget {
  @override
  final fileModel doc;
  String curruser;

  DocumentInfoPage({Key key, this.doc, this.curruser}) : super(key: key);

  _DocumentInfoPageState createState() => _DocumentInfoPageState();
}

class _DocumentInfoPageState extends State<DocumentInfoPage> {
  String currUserName;
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);

//  Future<void> fetchCurrUser() async {
//    print("curruser\n");
//    SharedPreferences prefs = await SharedPreferences.getInstance();
//    currUserName = prefs.getString('username');
//    print("fetch" + currUserName);
//  }

//  String getCurrUser() {
//    fetchCurrUser();
//    return currUserName;
//  }

//  void downloadFile() {
//    //download the file implementation
//  }
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
  Future<void> downloadFile() async {
    //download the file implementation
    HttpClient client = new HttpClient();
    var _downloadData = List<int>();
    var downloadPath = await getApplicationDocumentsDirectory();
    var fileSave  = new File(downloadPath.path + "/" + widget.doc.fileName);
    client.getUrl(Uri.parse(widget.doc.documentLink))
        .then((HttpClientRequest request) {
      return request.close();
    })
        .then((HttpClientResponse response) {
      response.listen((d) => _downloadData.addAll(d),
          onDone: () {
            fileSave.writeAsBytes(_downloadData);
          }
      );
    });

    var shared = await FlutterShare.shareFile(
      title: widget.doc.fileName,
      text: 'Share' + widget.doc.fileName,
      filePath:  fileSave.path,
    );
    if (shared != null) {
      createAlertDialog(context, "File downloaded");
    } else {
      createAlertDialog(context, "There was a problem downloading your file");
    }

  }
  changePreferences(bool val) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    //print(widget.groupObj.id.toString());

    http.Response r = await http.post(
        'http://134.122.21.105:8080/documents/' + widget.doc.id.toString() +
            '/set-shared',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        },
        body: jsonEncode(val)
    );

    if (r.statusCode == 200) {
      Navigator.pop(context);
//      Navigator.push(
//        context,
//        MaterialPageRoute(builder: (context) => DocumentInfoPage(doc: widget.doc, curruser: widget.curruser,)),
//      );
      val ?
      createAlertDialog(context, widget.doc.fileName + " is now being shared with group")
          :
      createAlertDialog(context, widget.doc.fileName + " is not being shared with the group anymore");

    }
    else {
      createAlertDialog(context, "Couldn't change sharing preferences for file " + widget.doc.fileName + "!");
    }
  }
  @override
  Widget build(BuildContext context) {
    //fetchCurrUser();

    MaterialButton downloadFileBtn = MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      height: 50,
      padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
      color: Color(0xff7ddcff),
      child: Text(
        "Download File",
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
        print(widget.doc.documentLink);
        showDialog(
            context: context,
            builder: (context) {
              return CupertinoAlertDialog (
                title: Text("Would you like to download " + widget.doc.fileName + " ?",
                    style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
                actions: <Widget>[

                  MaterialButton(
                      child: Text("Yes"),
                      onPressed: () async {
                        downloadFile();
                        Navigator.of(context).pop();
                      }
                  ),
                  MaterialButton(
                      child: Text("No"),
                      onPressed: () {
                        Navigator.of(context).pop();
                        createAlertDialog(context, "File not downloaded");

                      }
                  ),

                ],
              );
            });

      },
    );
    MaterialButton deleteFileBtn = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,
        padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),
        child: Text(
          "Delete File",
          textAlign: TextAlign.center,
          style: style.copyWith(fontFamily: 'Molengo'), //,
          //fontWeight: FontWeight.bold),
        ),
        textColor: Color(0xff535a70),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
        ),
        onPressed: () async {
          SharedPreferences prefs = await SharedPreferences.getInstance();
          String authT = prefs.getString('authToken');
          http.Response r = await http.delete('http://134.122.21.105:8080/documents/' + widget.doc.id.toString(),
              headers: {
                "Accept": "application/json",
                "Content-type": "application/json",
                "authorization": "bearer " + authT
              }
          );

          if (r.statusCode == 200){
            //Navigator.pop(context);
            await createAlertDialog(context, "File deleted successfully\nRefresh to see!");
            Navigator.pop(context);
          }
          else {
            createAlertDialog(context, "File could not be deleted");
            //Navigator.pop(context);
          }
        }//deletion
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
          backgroundColor: Colors.white,
          title: Text("Document Info",
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
                  Icon(Icons.insert_drive_file,
                      color: Color(0xff535a70),
                      size: MediaQuery.of(context).size.width / 5),
                  Text(widget.doc.fileName,
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery
                              .of(context).size.width / 20)),
                  SizedBox(height:10),
                  Text( widget.doc.uploadDate.substring(5,7) + "/" + widget.doc.uploadDate.substring(8,10) + "/" +
                      widget.doc.uploadDate.substring(0,4) + " " + widget.doc.uploadDate.substring(11),
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery.of(context).size.width / 25)),
                  SizedBox(height:10),
                  Text("Owner: " + widget.doc.uploader,
                      style: TextStyle(
                          color: Color(0xff535a70),
                          fontFamily: "Molengo",
                          fontSize: MediaQuery.of(context).size.width / 20)),
                  SizedBox(height:10),
                  (widget.doc.uploader == widget.curruser)
                      ?
                  Center(child:Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      Switch(
                        value: widget.doc.shared,
                        onChanged: (value) {
//                          setState(() {
                            widget.doc.shared = value;
                            changePreferences(value);
//                          });
                        },
                        activeTrackColor: Color(0xff7ddcff),
                        activeColor: Color(0xff1e6b87),
                      ),
                      Text("Make File Visible",
                          style: TextStyle(
                              color: Color(0xff535a70),
                              fontFamily: "Molengo",
                              fontSize:
                              MediaQuery.of(context).size.width / 20, ))
                    ],
                  ))
                      : SizedBox(height: 15),
                  downloadFileBtn,
                  SizedBox(height:10),
                  (widget.doc.uploader == widget.curruser) ?
                  deleteFileBtn : SizedBox(height: 15),
                ],
              ),
            )
          ],
        )

    );
  }
}