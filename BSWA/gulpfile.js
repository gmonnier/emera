var gulp = require('gulp')
var concat = require('gulp-concat')
var uglify = require('gulp-uglify')
var ngAnnotate = require('gulp-ng-annotate')

gulp.task('js', function () {
  gulp.src(['src/main/webapp/resources/javascript/**/angular-app-init.js', 'src/main/webapp/resources/javascript/**/*.js'])
    .pipe(concat('src/main/webapp/resources/javascript/angular-app-compiled.js'))
    .pipe(ngAnnotate())
    .pipe(uglify())
    .pipe(gulp.dest('.'))
})