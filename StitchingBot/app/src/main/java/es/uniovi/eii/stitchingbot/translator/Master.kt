package es.uniovi.eii.stitchingbot.translator

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.*

class Master(private val numberOfThreads: Int, private val data: ByteArray, val matrixHeight: Int, private val matrixWidth: Int) {

    val pointsMatrix = Array(matrixHeight) {
        IntArray(matrixWidth)
    }

    val coordsArray = mutableListOf<Pair<Int,Int>>()
    //val latch: CountDownLatch = CountDownLatch(numberOfThreads)

    @RequiresApi(Build.VERSION_CODES.O)
    fun compute() {
        val workers = arrayListOf<Worker>()
        val elementsPerThread = data.size / numberOfThreads

        for (i in 0 until numberOfThreads) {
            workers.add(Worker(
                data, i * elementsPerThread,
                if (i < numberOfThreads - 1) (i + 1) * elementsPerThread - 1 else data.size - 1,
                matrixHeight/numberOfThreads, matrixWidth
            ))
        }

        val threads = mutableListOf<Thread>()
        for(i in 0 until workers.size){
            threads.add(Thread(workers[i]))
            threads[i].start()
        }

        for(thread in threads){
            thread.join()
        }
        //latch.await()

        val result = mutableListOf<Int>()
        for(worker in workers){
            result.addAll(worker.result)
            coordsArray.addAll(worker.coordsArray)
        }

        for(coord in coordsArray){
            pointsMatrix[coord.second][coord.first]=1
        }


    }


    fun compute2() {
        val workers = arrayListOf<WorkerV2>()
        val elementsPerThread = data.size / numberOfThreads

        for (i in 0 until numberOfThreads) {
            workers.add(WorkerV2(
                data, i * elementsPerThread,
                if (i < numberOfThreads - 1) (i + 1) * elementsPerThread - 1 else data.size - 1,
                matrixHeight/numberOfThreads, matrixWidth
            ))
        }

        val executor = Executors.newFixedThreadPool(numberOfThreads)

        val threads = mutableListOf<Future<MutableList<Pair<Int,Int>>>>()

        for(i in 0 until workers.size){
            threads.add(executor.submit(workers[i]))

        }

        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)


        for(thread in threads){
            coordsArray.addAll(thread.get())
        }

        for(coord in coordsArray){
            pointsMatrix[coord.second][coord.first]=1
        }


    }
}