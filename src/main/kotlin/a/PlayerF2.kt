package GUISamples

//import Notify.Notify
import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Duration
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor


class PlayerF2: Application()  {

    internal var selectedFile: File? = null
    internal var player: MediaPlayer? = null
    internal var musicSlider = Slider()
    internal var playableNumber: Int = 0
    internal var mplayer: MediaPlayer? = null
    internal var players: MutableList<MediaPlayer> = ArrayList()
//    internal var ListOfTrack: MutableList<InfoToTable> = ArrayList()
    internal var listOfTracks: ObservableList<InfoToTable> = FXCollections.observableArrayList<InfoToTable>()
    internal var table = TableView<InfoToTable>(listOfTracks)

    internal var numberOfTrack = TableColumn<InfoToTable, String>("Number")
    internal var nameOfTrack = TableColumn<InfoToTable, String>("Name")
    internal var timeOfTrack = TableColumn<InfoToTable, String>("Time")


//    internal var source: String? = null

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {

        table.prefHeight = 300.0
        table.prefWidth = 600.0


        numberOfTrack.setCellValueFactory(PropertyValueFactory<InfoToTable, String>("numbertrack"))
        numberOfTrack.minWidth = 100.0
        nameOfTrack.setCellValueFactory(PropertyValueFactory("nametrack"))
        nameOfTrack.minWidth = 250.0
        timeOfTrack.setCellValueFactory(PropertyValueFactory<InfoToTable, String>("timetrack"))
        timeOfTrack.minWidth = 250.0

        var selectionM = table.selectionModel
        selectionM.setSelectionMode(SelectionMode.SINGLE);
        var selectedId = selectionM.selectedIndices

        //var notify = Notify()
        //notify.config.mode = 1
        var okBtn = Button()
        okBtn.text = "OK"
        okBtn.onAction = EventHandler {
            mplayer!!.pause()
            //notify.closeAnimationPt()
        }
        //notify.config.okBtnCallback(okBtn)


        val fileChooser = FileChooser()
        fileChooser.title = "Open File"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
        )

        val menu = Menu("File")

        val selectMenuItem = MenuItem("Select")
        selectMenuItem.setOnAction { EventHandler ->
            selectedFile = fileChooser.showOpenDialog(primaryStage)
            if (selectedFile != null) {
                players.add(selectFileToPlayer(selectedFile!!))

                if (playableNumber == 0 && players.size == 1)
                {
                    mplayer = players[players.size - 1]
                    mplayer!!.play()
                    //notify.startNotify()
                }
                else
                {
                    mplayer?.stop()
                    //notify.startNotify()
                    var prevVolumeRate = playableNumber
                    playableNumber = players.size - 1
                    mplayer = players[playableNumber]
                    mplayer?.volume = players[prevVolumeRate].volume
                    mplayer?.rate = players[prevVolumeRate].rate
                    mplayer?.play()

                }

//                var temp = players.get(players.size - 1)
//                pickTheNameOfTrack(temp)

                println(players.size)
            }
        }
        menu.items.addAll(selectMenuItem)
        var menuBar = MenuBar(menu)

        var mediaView = MediaView()


        val prevButton = Button("Prev")
        prevButton.onAction = EventHandler {
            if (players.size != 0 && playableNumber < players.size && playableNumber > 0)
            {
//                players.get(playableNumber).stop()
//                playableNumber -= 1
//                players.get(playableNumber).volume = players.get(playableNumber + 1).volume
//                players.get(playableNumber).play()

                mplayer = players[playableNumber]
                mplayer?.stop()
                playableNumber -= 1
                mplayer = players[playableNumber]
                mplayer?.volume = players[playableNumber + 1].volume
                mplayer?.rate = players[playableNumber + 1].rate
                mplayer?.play()
            }
            else
            {
                println("Error")
            }
        }

        val nextButton = Button("Next")
        nextButton.onAction = EventHandler {
            if (players.size != 0 && playableNumber < players.size - 1 && playableNumber >= 0)
            {
//                players.get(playableNumber).stop()
//                playableNumber += 1
//                players.get(playableNumber).volume = players.get(playableNumber - 1).volume
//                players.get(playableNumber).play()
                //notify.startNotify()
                mplayer = players[playableNumber]
                mplayer?.stop()
                playableNumber += 1
                mplayer = players[playableNumber]
                mplayer?.volume = players[playableNumber - 1].volume
                mplayer?.rate = players[playableNumber - 1].rate
                mplayer?.play()

            }
            else
            {
                println("Error")
            }
        }

        val playButton = Button("Play")
        playButton.onAction = EventHandler {
            if (players.size != 0)
            {
//                println(selectedId)
//                var str = selectedId.toString()
//                str = str.toString().replace("[", "").replace("]", "")
//                str = str.toInt().toString()
//
//                println(str)
                mplayer = players[playableNumber]
//                players.get(playableNumber).play()
                mplayer?.play()
//                pickTheNameOfTrack(mplayer!!)

            }
            else
            {
                println("Nothing to Play")
            }
        }

        val pauseButton = Button("Pause")
        pauseButton.onAction = EventHandler {
            if (players.size != 0)
            {
                mplayer = players[playableNumber]
//                players.get(playableNumber).pause()
                mplayer?.pause()
            }
            else
            {
                println("Nothing to Pause")
            }
        }

        val stopButton = Button("Stop")
        stopButton.onAction = EventHandler {
            if (players.size != 0)
            {
                mplayer = players[playableNumber]
//                players.get(playableNumber).stop()
                mplayer?.stop()
            }
            else
            {
                println("Nothing to Stop")
            }
        }

        val volumeSlider = Slider(0.0, 1.0, 0.5)
        volumeSlider.majorTickUnit = 0.1
        volumeSlider.isShowTickLabels = true
        volumeSlider.valueProperty().addListener(InvalidationListener {
            players[playableNumber].volume = volumeSlider.value
        })

        val musicSlider = Slider()
        musicSlider.min = 0.0
        musicSlider.max = 100.0
        musicSlider.valueProperty().addListener(InvalidationListener {
            if (musicSlider.isValueChanging)
                mplayer?.seek(Duration.seconds(musicSlider.value.toDouble()))
//            else
//                musicSlider.value =  players.get(playableNumber).currentTime!!.toSeconds()
        })

        val rateSlider = Slider(0.0, 8.0, 1.0)
        rateSlider.majorTickUnit = 1.0
        rateSlider.isShowTickLabels = true
        rateSlider.valueProperty().addListener(InvalidationListener {
            mplayer?.rate = rateSlider.value
        })


        Thread(Runnable {
            while (true) {
                if (mplayer != null) {

                    mplayer?.currentTimeProperty()
                        ?.addListener { observable, oldValue, newValue -> musicSlider.value = newValue.toSeconds() }
                    musicSlider.maxProperty().bind(
                        Bindings.createDoubleBinding(
                            { mplayer?.getTotalDuration()?.toSeconds() },
                            mplayer?.totalDurationProperty()
                        )
                    )
                    musicSlider.value = mplayer?.currentTime!!.toSeconds()

                    mplayer?.onEndOfMedia = Runnable {
                        mplayer?.seek(Duration.seconds(0.0))
                        mplayer?.stop()

                        if (players.size != 0 && playableNumber < players.size - 1 && playableNumber >= 0) {

                            playableNumber += 1
                            mplayer = players[playableNumber]
                            mplayer?.volume = players[playableNumber - 1].volume
                            mplayer?.rate = players[playableNumber - 1].rate
                            mplayer?.play()

                        }
                    }


                    println("Cur time " + mplayer?.currentTime!!.toSeconds())
                }
                try {
                    Thread.sleep(900)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()

        table.setRowFactory {
            var row: TableRow<InfoToTable> = TableRow()
            row.setOnMouseClicked { e ->
                if (e.clickCount == 2 && !row.isEmpty)
                {
                    mplayer?.stop()
                    var str = selectedId.toString()
                    str = str.toString().replace("[", "").replace("]", "")
                    str = str.toInt().toString()
                    var prevVolumeRate = playableNumber
                    playableNumber = str.toInt()
                    mplayer = players[playableNumber]
                    mplayer?.volume = players[prevVolumeRate].volume
                    mplayer?.rate = players[prevVolumeRate].rate
                    mplayer?.play()
                }

            }

            row
        }

        table.columns.addAll(numberOfTrack, nameOfTrack, timeOfTrack)

        val sliderPane = GridPane()
        sliderPane.vgap = 10.0
        sliderPane.hgap = 5.0

        table.style = "-fx-background-color: #9bb1fa"
        numberOfTrack.style = "-fx-background-color: #9bb1fa"
        nameOfTrack.style = "-fx-background-color: #9bb1fa"
        timeOfTrack.style = "-fx-background-color: #9bb1fa"
        sliderPane.style = "-fx-background-color: #9bb1fa"
        musicSlider.style = "-fx-background-color: #9bb1fa"

        sliderPane.addRow(0, Label("Volume"), volumeSlider)
        sliderPane.addRow(1, Label("Rate"), rateSlider)

        val flowPane = FlowPane(10.0, 10.0, table)

        val hBox = HBox(5.0, prevButton, nextButton, playButton, pauseButton, stopButton)
        val stackLayout = VBox(menuBar, hBox, sliderPane, musicSlider, flowPane)
        var scene = Scene(stackLayout, 600.0, 400.0)






        primaryStage.title = "Player"
        primaryStage.scene = scene
        primaryStage.show()
    }


    private fun selectFileToPlayer(selectedFile: File): MediaPlayer {
        val tempPlayer = MediaPlayer(Media(selectedFile.toURI().toString()))
        tempPlayer.onError = Runnable {
            println("Media error occurred: " + tempPlayer.error)
        }

        tempPlayer.onReady = Runnable {
            pickTheNameOfTrack(tempPlayer)
        }


//        tempPlayer.play()

        return tempPlayer
    }

    private fun pickTheNameOfTrack(mplayer: MediaPlayer)
    {
//        mplayer.play()
        var source = mplayer?.media?.source
        source = source?.substring(0, source.length - ".mp3".length)
        source = source?.substring(source?.lastIndexOf("/")!! + 1)?.replace("%20", " ")

        var timeOfTrack = mplayer?.stopTime!!.toSeconds().toDouble()
        var minutes = floor(timeOfTrack / 60)
        var seconds = ceil(timeOfTrack) - (minutes * 60)
        var timeTr = minutes.toInt().toString() + " minutes "

        if (seconds > 59.0)
        {
            seconds = 0.0
            minutes += 1.0
            timeTr = minutes.toInt().toString() + " minutes "
        }
        else
        {
            timeTr += seconds.toInt().toString() + " seconds"
        }

        var metaTable : InfoToTable = InfoToTable(players.size.toString(), source.toString(), timeTr)

        listOfTracks.add(metaTable)

        println("$source : $minutes minutes $seconds seconds")
//        mplayer.stop()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PlayerF2::class.java)
        }
    }
}

class InfoToTable constructor(numbertrack: String, nametrack: String, timetrack: String) {
    val numbertrack: String
    val nametrack: String
    val timetrack: String

    fun getNumberTrack(): String {
        return numbertrack
    }

    fun getNameTrack(): String {
        return nametrack
    }

    fun getTimeTrack(): String {
        return timetrack
    }

    init {
        this.numbertrack = numbertrack
        this.nametrack = nametrack
        this.timetrack = timetrack
    }
}