package com.example.rksishedule.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

object DataSettingsKeys {
    val USER_ENTITY = stringPreferencesKey("user_entity")
    val USER_VIEW = stringPreferencesKey("user_view")
    val SHEDULE_LINK = stringPreferencesKey("shedule_link")
}