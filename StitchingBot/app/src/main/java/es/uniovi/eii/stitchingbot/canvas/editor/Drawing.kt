package es.uniovi.eii.stitchingbot.canvas.editor

class Drawing {

    fun addFigure(figura: Figure) {
        figuras.add(figura)
    }

    fun draw() {
        for (figura in figuras) figura.draw()
    }

    fun getFigure(x: Int, y: Int): Figure? {
        for (figura in figuras) if (figura.contain(x, y)) return figura
        return null
    }

    var figuras: MutableList<Figure> = ArrayList<Figure>()
}