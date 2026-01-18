package com.example.pivota.core.presentations.navigation

import kotlinx.serialization.Serializable

/* ───────── ENTRY ───────── */
@Serializable
object Welcome

/* ───────── GUEST FLOW ───────── */
@Serializable
object Discovery          // Preferences / intent selection (guest)

@Serializable
object GuestDashboard     // Browse listings as guest

/* ───────── AUTH FLOW (ON-DEMAND) ───────── */
@Serializable
object AuthFlow

@Serializable
object Register

@Serializable
object Login

/* ───────── FULL ACCESS ───────── */
@Serializable
object Dashboard
