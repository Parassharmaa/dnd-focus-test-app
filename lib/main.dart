import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Testing App'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var platform = const MethodChannel('app.tummo.test_flickering');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: SingleChildScrollView(
        child: SizedBox(
          width: MediaQuery.maybeOf(context)!.size.width,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              const SizedBox(height: 20),
              const Text(
                'Test Mode On',
                style: TextStyle(fontSize: 20),
              ),
              const SizedBox(height: 100),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("grantNotificationPolicyAccess");
                },
                child: const Text("Grant Permission"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("setInteruptionFilter", {"filter": 2});
                },
                child: const Text("Turn on DND"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("setInteruptionFilter", {"filter": 1});
                },
                child: const Text("Turn off DND"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("requestOverlayPermission");
                },
                child: const Text("Request Overlay"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("startAppblockService");
                },
                child: const Text("Start Service"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("stopAppblockService");
                },
                child: const Text("Stop Service"),
              ),
              TextButton(
                onPressed: () async {
                  var res =
                      await platform.invokeMethod("isAppblockServiceRunning");
                  print(res);
                },
                child: const Text("Service Status"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("grantUsagePermission");
                },
                child: const Text("Usage Permission"),
              ),
              TextButton(
                onPressed: () {
                  platform.invokeMethod("grantNotificationAccess");
                },
                child: const Text("Notification Permission"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
