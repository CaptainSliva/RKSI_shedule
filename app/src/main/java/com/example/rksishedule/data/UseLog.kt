package com.example.rksishedule.data
//за сеанс
//за день
//за месяц
//за семестр
data class UseLog(
    var UID: String,
    var mainEntity: String, // Сущность по умолчанию
    var amountOfStarts: Int, // Запуски приложения
    var amountOfViewOne: Int, // Нажатие на кнопку для большой карточки
    var amountOfViewThree: Int, // Нажатие на кнопку для средней картчки
    var amountOfViewEasy: Int, // Нажатие на кнопку для маленькой карточки
    var amountOfViewChanges: Int, // Количество изменений отображения
    var amountOfOpenMainListgroups: Int, // Нажатие на выбор группы
    var amountOfOpenMainListPreps: Int, // Нажатие на выбор препода
    var amountOfRefresh: Int, // Нажатие на перезагрузку страницы

    var amountOfLessonClick: Int, // Нажатий на карточки
    var amountOfDelete: Int,  // Удалений пары кнопкой
    var amountOfEdit: Int, // Изменений пары кнопкой
    var amountOfEditNumberLesson: Int, // Изменений номера пары
    var amountOfEditLesson: Int, // Изменений пары
    var amountOfEditEntity: Int, // Изменений сущности
    var amountOfEditAudience: Int, // Изменений аудитории

    var amountOfTurtleEdit: Int, // Количество изменения расписания черепахой

    var amountOLessonsTimeView: Int, // Количество просмотров расписания пар

    var amountOfSheduleClick: Int, // Количество нажатия на кнопку для вывода информации с планшетки
    var amountOfChangeGroup: Int, // Количество выбора группы
    var amountOfChangePrep: Int, // Количество выбора препода
    var amountOfChangeDate: Int, // Количество выбора другой даты
    var amountOfChooseThisLesoonNumber: Int, // Сколько раз нужна была конкретная пара

    var amountOfChangesMainEntity: Int, // Количество изменений главной сущности
    var amountOfClearBaseSheduleChanges: Int, // Количество очистки базы изменнений расписания
    var amountOfClearBaseLessonNames: Int, // Количество  очистки базы названий пар

    var amountOfTurtleError: Int, // Количество ошибок при изменнении расписания ччерепахой
    var turtleErrorText: String, // Полученный текст при измененнии расписания черепахой из-за которого возникла ошибка
    var amountOfSheduleError: Int, // Количество ошибок при парсинге планшетки
    var typeOfSheduleError: Int, // Вид ошибки при парсинге планшетки
    var amountOfCrashes: Int, // Количество крашей приложения

    var amountOfWindowsTravels: Int, // Количество путешествий по окнам приложения
    var amountOfClickBackButton: Int, // Количество нажатий на кнопку назад
)
