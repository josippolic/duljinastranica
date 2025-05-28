import java.awt.*
import java.awt.event.*
import javax.swing.*
import kotlin.math.*

class TrokutApp : JFrame() {
    private val t1xField = JTextField(5)
    private val t1yField = JTextField(5)
    private val t2xField = JTextField(5)
    private val t2yField = JTextField(5)
    private val t3xField = JTextField(5)
    private val t3yField = JTextField(5)
    private val areaLabel = JLabel("Površina: ")
    private val sidesLabel = JLabel("Stranice: ")
    private val gridSizeBox = JComboBox(
        arrayOf("10x10", "15x15", "20x20", "25x25", "30x30", "35x35", "40x40", "45x45", "50x50")
    )
    private val drawPanel = DrawPanel()

    private var x1 = 0.0
    private var y1 = 0.0
    private var x2 = 0.0
    private var y2 = 0.0
    private var x3 = 0.0
    private var y3 = 0.0
    private var shouldDraw = false
    private var gridCount = 10

    init {
        title = "Trokut - površina i prikaz"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 700)
        setLocationRelativeTo(null)
        layout = BorderLayout()

        val inputPanel = JPanel(GridLayout(4, 1))

        val row1 = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(JLabel("T1x:")); add(t1xField)
            add(JLabel("T1y:")); add(t1yField)
            add(JLabel("T2x:")); add(t2xField)
            add(JLabel("T2y:")); add(t2yField)
            add(JLabel("T3x:")); add(t3xField)
            add(JLabel("T3y:")); add(t3yField)
        }

        val row2 = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(JLabel("Veličina mreže:"))
            gridSizeBox.selectedItem = "10x10"
            add(gridSizeBox)
        }

        val row3 = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            val izracunajBtn = JButton("Izračunaj")
            val resetBtn = JButton("Reset")
            val izlazBtn = JButton("Izlaz")
            add(izracunajBtn)
            add(resetBtn)
            add(izlazBtn)
            izracunajBtn.addActionListener { izracunaj() }
            resetBtn.addActionListener { reset() }
            izlazBtn.addActionListener { System.exit(0) }
        }

        val row4 = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(areaLabel)
            add(sidesLabel)
        }

        inputPanel.add(row1)
        inputPanel.add(row2)
        inputPanel.add(row3)
        inputPanel.add(row4)

        add(inputPanel, BorderLayout.NORTH)
        add(drawPanel, BorderLayout.CENTER)

        gridSizeBox.addActionListener {
            val selected = gridSizeBox.selectedItem as String
            gridCount = selected.split("x")[0].toInt()
            drawPanel.repaint()
        }
    }

    private fun izracunaj() {
        try {
            x1 = t1xField.text.toDouble()
            y1 = t1yField.text.toDouble()
            x2 = t2xField.text.toDouble()
            y2 = t2yField.text.toDouble()
            x3 = t3xField.text.toDouble()
            y3 = t3yField.text.toDouble()

            val a = distance(x2, y2, x3, y3)
            val b = distance(x1, y1, x3, y3)
            val c = distance(x1, y1, x2, y2)

            val area = abs(
                (x1 * (y2 - y3) +
                 x2 * (y3 - y1) +
                 x3 * (y1 - y2)) / 2.0
            )

            areaLabel.text = "Površina: %.2f".format(area)
            sidesLabel.text = "Stranice: a=%.2f, b=%.2f, c=%.2f".format(a, b, c)

            shouldDraw = true
            drawPanel.repaint()
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this, "Unesite ispravne brojeve (decimalni su dozvoljeni).")
        }
    }

    private fun reset() {
        t1xField.text = ""
        t1yField.text = ""
        t2xField.text = ""
        t2yField.text = ""
        t3xField.text = ""
        t3yField.text = ""
        areaLabel.text = "Površina: "
        sidesLabel.text = "Stranice: "
        gridCount = 10
        gridSizeBox.selectedItem = "10x10"
        shouldDraw = false
        drawPanel.repaint()
    }

    private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
    }

    inner class DrawPanel : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g as Graphics2D
            val width = width
            val height = height
            val offsetX = width / 2
            val offsetY = height / 2
            val scale = min(width, height) / (2 * gridCount)

            // Mreža
            g2.color = Color.LIGHT_GRAY
            for (i in -gridCount..gridCount) {
                val x = offsetX + i * scale
                val y = offsetY - i * scale
                g2.drawLine(x, 0, x, height)
                g2.drawLine(0, y, width, y)
            }

            // Osi
            g2.color = Color.BLACK
            g2.stroke = BasicStroke(2f)
            g2.drawLine(0, offsetY, width, offsetY)
            g2.drawLine(offsetX, 0, offsetX, height)

            if (shouldDraw) {
                val x1Pix = offsetX + (x1 * scale).toInt()
                val y1Pix = offsetY - (y1 * scale).toInt()
                val x2Pix = offsetX + (x2 * scale).toInt()
                val y2Pix = offsetY - (y2 * scale).toInt()
                val x3Pix = offsetX + (x3 * scale).toInt()
                val y3Pix = offsetY - (y3 * scale).toInt()

                g2.color = Color.BLUE
                g2.stroke = BasicStroke(2f)
                g2.drawPolygon(intArrayOf(x1Pix, x2Pix, x3Pix), intArrayOf(y1Pix, y2Pix, y3Pix), 3)

                g2.color = Color.RED
                g2.fillOval(x1Pix - 4, y1Pix - 4, 8, 8)
                g2.fillOval(x2Pix - 4, y2Pix - 4, 8, 8)
                g2.fillOval(x3Pix - 4, y3Pix - 4, 8, 8)

                g2.color = Color.BLACK
                g2.drawString("T1(${trim(x1)},${trim(y1)})", x1Pix + 10, y1Pix + 15)
                g2.drawString("T2(${trim(x2)},${trim(y2)})", x2Pix + 10, y2Pix + 15)
                g2.drawString("T3(${trim(x3)},${trim(y3)})", x3Pix + 10, y3Pix + 15)
            }
        }

        private fun trim(value: Double): String {
            return if (value == value.toInt().toDouble()) value.toInt().toString()
            else "%.2f".format(value)
        }
    }
}

fun main() {
    SwingUtilities.invokeLater {
        TrokutApp().isVisible = true
    }
}
