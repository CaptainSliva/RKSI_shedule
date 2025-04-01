package com.example.rksishedule.parser

import android.content.Context
import com.example.rksishedule.data.currentDay
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.sheduleLink
import com.example.rksishedule.utils.PlanshetInfo
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory


class ParsePlanshetteShedule {
    init {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
    }


    suspend fun findXlsxInfo(context: Context, currentEntity: String, sheduleParse: String): MutableList<PlanshetInfo> {
        try {
            val file = File("${context.filesDir}/${sheduleParse}.xlsx")
            FileInputStream(file)
        } catch (e: Exception) {
            return mutableListOf(PlanshetInfo("0", "0"))
        }


        entity = currentEntity.replace("  ", " ")
        println("ENTIT $entity $currentEntity")
        val file = File("${context.filesDir}/${sheduleParse}.xlsx")
        val inputStream = FileInputStream(file)
        val allPlanshetInfo = mutableListOf<PlanshetInfo>()
        val workbook = WorkbookFactory.create(inputStream)

        for (lessonNumber in 0..6) {
            val info: PlanshetInfo = PlanshetInfo()

            val workSheet = workbook.getSheetAt(lessonNumber)
            for (i in 1..50) {
                if (entity.contains("[0-9]".toRegex()) && workSheet.getRow(i) != null) {
                    if ((workSheet.getRow(i).getCell(1)?.toString() ?: "") == entity) {
                        if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                        info.secondEntity += workSheet.getRow(i).getCell(2)?.toString() ?: ""
                        info.aud += workSheet.getRow(i).getCell(0)?.toString() ?: ""
                    }
                    if ((workSheet.getRow(i).getCell(4)?.toString() ?: "") == entity) {
                        if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                        info.secondEntity += workSheet.getRow(i).getCell(5)?.toString() ?: ""
                        info.aud += workSheet.getRow(i).getCell(3)?.toString() ?: ""
                    }
                }

                if (!entity.contains("[0-9]".toRegex()) && workSheet.getRow(i) != null) {
                    if ((workSheet.getRow(i).getCell(2)?.toString() ?: "") == entity) {
                        if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                        info.secondEntity += workSheet.getRow(i).getCell(1)?.toString() ?: ""
                        info.aud += workSheet.getRow(i).getCell(0)?.toString() ?: ""
                    }
                    if ((workSheet.getRow(i).getCell(5)?.toString() ?: "") == entity) {
                        if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                        info.secondEntity += workSheet.getRow(i).getCell(4)?.toString() ?: ""
                        info.aud += workSheet.getRow(i).getCell(3)?.toString() ?: ""
                    }
                }
            }
            info.aud = info.aud.replace(".0", "")
            allPlanshetInfo.add(info)
        }

        inputStream.close()
        file.delete()

        allPlanshetInfo.forEach { info -> println("${info.aud} ${info.secondEntity}") }
        return allPlanshetInfo
    }


    suspend fun findXlsxInfo(context: Context, entity: String, lessonNumber: Int, sheduleParse: String): MutableList<PlanshetInfo> {
        if (lessonNumber < 0 || lessonNumber > 7) {
            return mutableListOf(PlanshetInfo("0", "", true))
        } else {
            try {
                val file = File("${context.filesDir}/${sheduleParse}.xlsx")
                FileInputStream(file)
            } catch (e: Exception) {
                return mutableListOf(PlanshetInfo("0", "0"))
            }
        }

        val file = File("${context.filesDir}/${sheduleParse}.xlsx")
        val inputStream = FileInputStream(file)
        val allPlanshetInfo = mutableListOf<PlanshetInfo>()
        var flagAdd = false

        val workbook = WorkbookFactory.create(inputStream)
        val workSheet = workbook.getSheetAt(lessonNumber - 1)

        for (i in 1..50) {
            val info = PlanshetInfo()
            if (entity.contains("[0-9]".toRegex()) && workSheet.getRow(i) != null) {
                if ((workSheet.getRow(i).getCell(1)?.toString() ?: "") == entity) {
                    if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                    info.secondEntity += workSheet.getRow(i).getCell(2)?.toString() ?: ""
                    info.aud += workSheet.getRow(i).getCell(0)?.toString() ?: ""
                    flagAdd = true
                }

                if ((workSheet.getRow(i).getCell(4)?.toString() ?: "") == entity) {
                    if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                    info.secondEntity += workSheet.getRow(i).getCell(5)?.toString() ?: ""
                    info.aud += workSheet.getRow(i).getCell(3)?.toString() ?: ""
                    flagAdd = true
                }
            }

            if (!entity.contains("[0-9]".toRegex()) && workSheet.getRow(i) != null) {
                if ((workSheet.getRow(i).getCell(2)?.toString() ?: "") == entity) {
                    if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                    info.secondEntity += workSheet.getRow(i).getCell(1)?.toString() ?: ""
                    info.aud += workSheet.getRow(i).getCell(0)?.toString() ?: ""
                    flagAdd = true
                }
                if ((workSheet.getRow(i).getCell(5)?.toString() ?: "") == entity) {
                    if (info.aud != "") info.aud += "\n"; if (info.secondEntity != "") info.secondEntity += "\n"
                    info.secondEntity += workSheet.getRow(i).getCell(4)?.toString() ?: ""
                    info.aud += workSheet.getRow(i).getCell(3)?.toString() ?: ""
                    flagAdd = true
                }
            }
            if (flagAdd) {
                info.aud = info.aud.replace(".0", "")
                allPlanshetInfo.add(info)
                flagAdd = false
            }
        }
        inputStream.close()
        file.delete()
        if (allPlanshetInfo.isEmpty()) {
            return mutableListOf(PlanshetInfo("", "", true))
        }
        allPlanshetInfo.forEach { info -> println("${info.aud} ${info.secondEntity}") }
        return allPlanshetInfo
    }

    suspend fun GSparseDays(): MutableList<String> {
        val url = sheduleLink
        var folder = ""
        try{
            folder = getUrlAsString(url)
        }catch (e: Exception) {}
        val regex_date = Regex("(?<=class=\"uXB7xe\" jsaction=\" mousedown:TiSivd;\" aria-label=)\"([^\"]+).xlsx")
        val find_days = mutableListOf<String>()
        regex_date.findAll(folder).forEach {
            find_days.add(it.value.slice(1..<it.value.length - 5))
        }
        for (i in 0..<find_days.size) {
            if (find_days[i] == normalizeDate(LocalDate.now().dayOfMonth, LocalDate.now().monthValue, LocalDate.now().year)) {
                currentDay = normalizeDate(LocalDate.now().dayOfMonth, LocalDate.now().monthValue, LocalDate.now().year)
            }
        }
        return find_days
    }

    suspend fun GSparse(context: Context, sheduleParse: String) {
        val url = sheduleLink
        var folder = ""
        try{
            folder = getUrlAsString(url)
        }catch (e: Exception) {}

        val regex_id = Regex("(?<=data-id=)\"([^\"]+)\"")
        val regex_date = Regex("(?<=class=\"uXB7xe\" jsaction=\" mousedown:TiSivd;\" aria-label=)\"([^\"]+).xlsx")
        val find_days = mutableListOf<String>()
        val find_ids = mutableListOf<String>()

        regex_id.findAll(folder)
            .forEach { i -> find_ids.add(i.value.slice(1..<i.value.length - 1)) }
        regex_date.findAll(folder)
            .forEach { i -> find_days.add(i.value.slice(1..<i.value.length - 5)) }
        println("find days - $find_days\nfind ids - $find_ids")
        for (i in 0..<find_days.size) {
            if (find_days[i] == sheduleParse) {
                println("${find_days[i]}\n${find_ids[i + 2]}\n\n")
                getDownload(context, find_ids[i + 2], find_days[i])
            }
        }



    }

    suspend fun getUrlAsString(url: String): String {
        val connectToSite = Jsoup.connect(url)
        val htmlText = connectToSite.get()
        return htmlText.toString()
    }

    suspend fun getDownload(context: Context, id: String, day: String) {
        val url =
            "https://drive.usercontent.google.com/download?id=${id}&export=download&authuser=0&confirm=t"
        val file = File("${context.filesDir}/${day}.xlsx")
//        try {
        val connection = URL(url).openConnection()
        connection.connect()
//            println("$file\n${connection.getInputStream()}")
        val inputStream: InputStream = connection.getInputStream()
        file.createNewFile()
        val outputStream: FileOutputStream = FileOutputStream(file)


        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        inputStream.close()
        outputStream.close()

        println("File downloaded successfully to ${day}.xlsx")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("Error downloading file: ${e.message}")
//        }
    }

    fun normalizeDate(vararg date: Int): String {
        val normD = mutableListOf<String>()
        for (i in 0.until(date.size)) {
            if (date[i] < 10) {
                normD.add("0${date[i]}")
            }
            else {
                normD.add("${date[i]}")
            }
        }
        return "${normD[0]}.${normD[1]}.${normD[2]}"
    }

}