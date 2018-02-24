import React from 'react';
import { Platform, PermissionsAndroid, StyleSheet, Alert, Text, View, Button } from 'react-native';
import StatusBarBackground from './src/components/StatusBarBackground';

export default class App extends React.Component {
  render() {
    return (
      
      <View style={styles.container}>
        <Text>Open up App.js to start working on your app!</Text>
        <Text>Changes you make will automatically reload.</Text>
        <Text>Shake your phone to open the developer menu.</Text>
        <Button title="Take a picture" onPress={this.requestCameraPermission} color="#841584" accessibilityLabel="Learn more about this purple button" />
      </View>
    
     /*
      <View style={styles.container}>
        <View>
          <StatusBarBackground style={{ backgroundColor: 'blue' }} />
        </View>
        <View style={{ flex: 1 }}>
          <View style={{ flex: 1 }}>
            <Text>jjkhkh</Text>
            <Text>Something in your eyes</Text>
          </View>
        </View>
        <Button title="Take a picture" onPress={this.launchCamera} color="#841584" accessibilityLabel="Learn more about this purple button" />
      </View>
      */
    );
  }

  async requestCameraPermission() {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA,
        {
          'title': 'Cool Photo App Camera Permission',
          'message': 'Cool Photo App needs access to your camera ' +
                     'so you can take awesome pictures.'
        }
      )
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log("You can use the camera")
      } else {
        console.log("Camera permission denied")
      }
    } catch (err) {
      console.warn(err)
    }
  }

  launchCamera() {
    Alert.alert('You tried starting the camera!');
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
