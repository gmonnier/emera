var gulp = require('gulp')
var concat = require('gulp-concat')

gulp.task('js', function () {
  gulp.src(['src/main/webapp/resources/javascript/**/angular-app-init.js', 'src/main/webapp/resources/javascript/**/*.js'])
    .pipe(concat('src/main/webapp/resources/javascript/angular-app-compiled.js'))
    .pipe(gulp.dest('.'))
})