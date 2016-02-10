var gulp = require('gulp');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var ngAnnotate = require('gulp-ng-annotate');

function handleError(err) {
  console.log(err.toString());
}

gulp.task('clean', function () {
    return gulp.src('src/main/webapp/resources/javascript/**/*-compiled.js', {read: false})
    .pipe(clean());
});

gulp.task('js', ['clean'], function () {
  gulp.src(['src/main/webapp/resources/javascript/**/angular-app-init.js', 'src/main/webapp/resources/javascript/**/*.js'])
    .pipe(concat('src/main/webapp/resources/javascript/angular-app-compiled.js'))
    .pipe(ngAnnotate())
    .pipe(uglify())
    .pipe(gulp.dest('.'))
    .on('error', handleError);
})

// Watch task - Eclude precompiled assets
gulp.task('watch', ['js'], function() {
  gulp.watch(['src/main/webapp/resources/javascript/**/*.js',"!src/main/webapp/resources/javascript/**/*-compiled.js"], ['js']);
});