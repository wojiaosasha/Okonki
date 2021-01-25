import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.stage.Stage
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.image.PixelWriter
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType
import org.w3c.dom.css.RGBColor
import java.awt.Checkbox
import java.awt.Toolkit
import java.awt.image.PixelGrabber
import java.io.Console
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

//-Xmx1024m
//-Xms1024m

enum class BackColor {
    WHITE,
    BLACK,
    COLOR,
    BLACKANDWHITE
}

class Sets : Application() {

    private var width = 700.0
    private var height = 700.0
    private val maxIterations = 100
    private val infinityBorder = 4

    override fun start(primaryStage: Stage?) {


        var userHeight = Toolkit.getDefaultToolkit().screenSize.height.toDouble()
        if (userHeight > 0) {
            width = userHeight - 90
            height = userHeight - 90
        }

        val root = HBox()
        val canvas = Canvas(width, height)
        val gc = canvas.graphicsContext2D.pixelWriter

        var backColor = BackColor.BLACK
        var setColor = Color.WHITE
        var a = -0.55
        var g=1.0
        var xam = 0
        var yam = 0

        var paintSet : ()->Unit = {mandelbrot(gc, setColor, backColor, xam, yam, g )}
        fun switch(key : Boolean) {
            if (key) //true - mandelbrot, false - julia
                paintSet = {mandelbrot(gc, setColor, backColor, xam, yam, g )}
            else
                paintSet = {julia(gc, backColor, a, xam, yam, g)}
        }
        fun scale(a : Int = 0, b : Int = 0) {
            xam += a
            yam += b
            g *= 0.95
            paintSet()
        }

        paintSet()

        var pane = object : FlowPane(Orientation.VERTICAL, 10.0, 10.0) {
            init {
                alignment = Pos.CENTER
                val man = CheckBox("Множество Мандельброта")
                val jul = CheckBox("Множество Жюлиа")
                var back = Label ("Фон:")
                val black = CheckBox("Черный")
                val white = CheckBox("Белый")
                val blackAndWhite = CheckBox("Черно-белый")
                val blue = CheckBox("Цветной")
                black.isSelected = true
                blue.setOnAction { _->
                    backColor = BackColor.COLOR
                    paintSet()
                    black.isSelected = false
                    white.isSelected = false
                    blackAndWhite.isSelected = false
                }
                black.setOnAction { _->
                    backColor = BackColor.BLACK
                    paintSet()
                    blue.isSelected = false
                    white.isSelected = false
                    blackAndWhite.isSelected = false
                }
                white.setOnAction { _->
                    backColor = BackColor.WHITE
                    paintSet()
                    black.isSelected = false
                    blue.isSelected = false
                    blackAndWhite.isSelected = false
                }
                blackAndWhite.setOnAction { _->
                    backColor = BackColor.BLACKANDWHITE
                    paintSet()
                    black.isSelected = false
                    blue.isSelected = false
                    white.isSelected = false
                }
                var fill = Label ("Цвет точек множества:")
                val fillBox = VBox(10.0)
                var whiteFill = CheckBox("Белый")
                var blackFill = CheckBox("Черный")
                whiteFill.isSelected = true
                whiteFill.setOnAction { _->
                    setColor = Color.WHITE
                    blackFill.isSelected = false
                    paintSet()
                }
                blackFill.setOnAction { _->
                    setColor = Color.BLACK
                    whiteFill.isSelected = false
                    paintSet()
                }
                fillBox.children.addAll(fill, whiteFill, blackFill)
                blackAndWhite.isDisable = true
                man.isSelected = true
                val step = Spinner<Double>(-0.8, 0.0, -0.55, 0.05)
                step.isDisable = true
                step.valueProperty().addListener() { new, _, _->
                    a = new.value
                    paintSet()
                }
                man.setOnAction { _->
                    jul.isSelected = false
                    fillBox.isDisable = false
                    step.isDisable = true
                    if (!whiteFill.isSelected)
                        whiteFill.fire()
                    if (!black.isSelected)
                        black.fire()
                    blackAndWhite.isDisable = true
                    switch(true)
                    paintSet()
                }
                jul.setOnAction {
                    if (!black.isSelected)
                        black.fire()
                    step.isDisable = false
                    man.isSelected = false
                    blackAndWhite.isDisable = false
                    fillBox.isDisable = true
                    switch(false)
                    paintSet()
                }
                val param = Label ("Параметр множества" +  "\n" +"Жулиа:")
                val scale = Button("Масштабировать")
                scale.setOnAction { _->
                    scale()
                    if (g<0.0001)
                        scale.isDisable = true
                }
                val removeScale = Button("Сбросить масштаб")
                removeScale.setOnAction {
                    scale.isDisable = false
                    g = 1.0
                    xam = 0
                    yam = 0
                    paintSet()
                }
                val help = Label("*Также масштабировать можно," + "\n" + "кликая по экрану")

                children.addAll(man, jul, back, black, white, blackAndWhite, blue, fillBox, param, step, scale, removeScale, help)


            }
        }
        root.children.addAll(canvas, pane)


        canvas.setOnMouseClicked { e->
            var x = e.x
            var y = e.y
            scale(((x-width/2)/g).toInt(), ((y-height/2)/g).toInt())
        }

        primaryStage?.scene?.fill = Paint.valueOf("WHITE")
        primaryStage?.maxWidth = userHeight + 200
        primaryStage?.scene = Scene(root)
        primaryStage?.show()



    }

    private fun mandelbrot (gc : PixelWriter, setColor : Color = Color.BLACK, backColor : BackColor = BackColor.WHITE, m : Int = 0, n : Int = 0, scale : Double = 1.0) { //, del : Paint

        var paint : (Int)->Color
        if (backColor == BackColor.BLACK)
            paint = {c -> Color.rgb(c, c, c) }
        else if (backColor == BackColor.WHITE)
            paint = {c -> Color.rgb(255-c, 255-c, 255-c)}
        else
            paint = {c -> Color.rgb(c, c/2, 170) }

        for (i in 0..width.toInt()) {
            for (j in 0..height.toInt()) {
                var result = true //принадлежит
                var c = 0
                var x = 0.0
                var y = 0.0
                for (k in 1..maxIterations) {
                    var x2 = x * x
                    var y2 = y * y
                    if (x2 + y2 > infinityBorder) {
                        result = false
                        c = (k.toDouble() / maxIterations * 255).toInt()
                        break
                    }
                    var xy = x * y
                    x = x2 - y2 + (3*(i+m)/width-2)*scale
                    y =  2 * xy + (3*(j+n)/height-1.5)*scale
                }
                if (result)
                    gc.setColor(i, j, setColor)
                else
                    gc.setColor(i, j, paint(c))
            }
        }

    }

    private fun julia (gc : PixelWriter, backColor: BackColor = BackColor.WHITE, a: Double = -0.55, m : Int = 0, n : Int = 0, scale : Double = 1.0) {

        var setColor = Color.WHITE
        var paint : (Int)->Color

        if (backColor == BackColor.WHITE) {
            paint = {c -> Color.rgb(255-c, 255-c, 255-c)}
            setColor = Color.BLACK
        }
        else if (backColor == BackColor.BLACK)
            paint = {c -> Color.rgb(c, c, c) }
        else if (backColor == BackColor.BLACKANDWHITE)
            paint = {c ->
                when (c % 2) {
                    0 -> Color.rgb(c, c, c)
                    else -> Color.rgb(255-c, 255-c, 255-c)
                }
            }
        else
            paint = { c->
                when (c % 3) {
                    0 -> Color.rgb(255, c, c)
                    1 -> Color.rgb(c, 255, c)
                    else->Color.rgb(c, c, 255)
                }
            }

        for (i in 0..width.toInt()) {
            for (j in 0..height.toInt()) {
                var result = true //принадлежит
                var c = 0
                //var a = -0.55 //от 0 до -0.8
                //var b = -0.3
                var x = (3*(i+m)/width - 1.5)*scale
                var y = (3*(j+n)/height - 1.5)*scale
                for (k in 1..maxIterations) {
                    var x2 = x * x
                    var y2 = y * y
                    if (x2 + y2 > infinityBorder) {
                        result = false
                        c=(k.toDouble()/maxIterations*255).toInt()
                        break
                    }
                    var xy = x * y
                    x = x2 - y2 + a
                    y =  2 * xy + a
                }
                if (!result)
                    gc.setColor(i, j, paint(c))
                else
                    gc.setColor(i, j, setColor)
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Sets::class.java)
        }
    }
}