package org.odk.collect.android.projects

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.odk.collect.android.R
import org.odk.collect.projects.Project

@RunWith(AndroidJUnit4::class)
class ProjectListItemViewTest {

    private val context: Context by lazy { ApplicationProvider.getApplicationContext() }

    @Test
    fun `shows project name`() {
        val view = ProjectListItemView(context)
        view.project = Project("SOM", "S", "#ffffff")
        assertThat(view.findViewById<TextView>(R.id.project_name).text, equalTo("SOM"))
    }

    @Test
    fun `shows project icon with color as background`() {
        val view = ProjectListItemView(context)
        view.project = Project("SOM", "S", "#ffffff")
        assertThat(view.findViewById<TextView>(R.id.project_icon).text, equalTo("S"))

        val background = view.findViewById<TextView>(R.id.project_icon).background as GradientDrawable
        assertThat(background.color!!.defaultColor, equalTo(Color.parseColor("#ffffff")))
    }
}
