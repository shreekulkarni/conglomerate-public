import 'package:conglomerate_ui/fileModel.dart';
import 'package:conglomerate_ui/folderModel.dart';

import 'folderModel.dart';


class documents {
  List<fileModel> files;
  List<folderModel> folders;


  documents({this.files, this.folders});

  factory documents.fromJson(Map<String, dynamic> json) {
    var docs = json['documents'] as List;
    var dir = json['folders'] as List;
    List<fileModel> file = new List();
    for (var f in docs) {
      file.add(fileModel.fromJson(f));
    }
    List<folderModel> folder = new List();
    print(dir);
    for (var d in dir) {
      folder.add(folderModel.fromJson(d));
    }

   // var folders = json['folders'] as List;

    return new documents (
      files: file,
      folders: folder

    );
  }
}

//TODO: update according to endpoint
class document {
  String filename;
  String fileID;
  String timestamp;
  String ownerName;
  bool privacy;
  document({this.filename, this.fileID, this.timestamp, this.ownerName, this.privacy});
}