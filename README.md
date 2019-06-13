# ProtocolBuilder

> This ProtocolBuilder is used to generate java classes from the game's sources.

The builder generates all the **Messages**, **Types** and **Enums** related to the game. It will also generate a JSON 
file composed of the classes (Messages and Types) and their ID. 

[![GIF](https://media.discordapp.net/attachments/481628413602299905/588810877923491874/gifProtocolBuilder.gif)]()


## Usage 

To use the Protcol Builder, either :

- Use `jar/protocol-builder-1.0-jar-with-dependencies.jar`

or 

- Clone the repository and start the class Main.java

You can also create a jar by running `mvn clean package`.

## Arguments

The builder needs a DofusInvoker.swf in order to extract the data.

```
 -f,--file <DofusInvoker.swf>   Full path to the DofusInvoker.swf
 -o,--output <outputDir>        Full path to the output directory
```

## License

[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

- **[MIT license](http://opensource.org/licenses/mit-license.php)**