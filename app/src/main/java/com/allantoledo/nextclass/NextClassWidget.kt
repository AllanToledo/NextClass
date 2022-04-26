package com.allantoledo.nextclass

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class NextClassWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val database = getNextClassDataBase(context)
        val collegeClassDao = database.collegeClassDao()
        val collegesClass = collegeClassDao.getAll()
        database.close()
        var nextClass = CollegeClassDao.getDefault()
        if (collegesClass.isNotEmpty())
            nextClass = collegesClass[0]

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val weekDayToday = DayOfWeek.of(
           if(today > 1) today - 1 else 7
        )
        val hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minuteNow = Calendar.getInstance().get(Calendar.MINUTE)

        val absoluteTimeNow = minuteNow + (hourNow * 60) + (weekDayToday.value * 24 * 60)

        for (collegeClass in collegesClass) {
            if (collegeClass.absoluteTime > absoluteTimeNow) {
                nextClass = collegeClass
                break
            }
        }
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, nextClass)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    nextClass: CollegeClass
) {
    val classTitle = nextClass.matter
    val classRoom = nextClass.locale
    val dayOfWeek = DayOfWeek.of(nextClass.dayOfWeek).getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("PT-BR"))
    val classStartAt = "${
        nextClass.hour.toString().padStart(2, '0')
    }:${
        nextClass.minute.toString().padStart(2, '0')
    }"
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.next_class_widget)
    views.setTextViewText(R.id.classTitle, classTitle)
    views.setTextViewText(R.id.classRoom, classRoom)
    views.setTextViewText(R.id.classStartAt, classStartAt)
    views.setTextViewText(R.id.dayOfWeek, dayOfWeek)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}