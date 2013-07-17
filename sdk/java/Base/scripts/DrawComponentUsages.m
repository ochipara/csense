function DrawComponentUsages(path)
load(sprintf('%s/%s', path, 'route_fileMonitor_station_fileMonitor.mat'));
load(sprintf('%s/%s', path, 'route_fileMonitor_station_form.mat'));
load(sprintf('%s/%s', path, 'route_fileMonitor_station_uploadComplete.mat'));
load(sprintf('%s/%s', path, 'route_fileMonitor_station_tapFilenameType0.mat'));

load(sprintf('%s/%s', path, 'route_form_station_form.mat'));
load(sprintf('%s/%s', path, 'route_form_station_httpUpload.mat'));
load(sprintf('%s/%s', path, 'route_form_station_uploadComplete.mat'));
load(sprintf('%s/%s', path, 'route_form_station_tapHTMLFormMessage0.mat'));

load(sprintf('%s/%s', path, 'route_gps_station_gps.mat'));
load(sprintf('%s/%s', path, 'route_gps_station_gpsLogger.mat'));
load(sprintf('%s/%s', path, 'route_gps_station_tapGPSMessage0.mat'));

load(sprintf('%s/%s', path, 'route_gpsLogger_station_gpsLogger.mat'));
load(sprintf('%s/%s', path, 'route_gpsLogger_station_syncQueue0.mat'));
load(sprintf('%s/%s', path, 'route_gpsLogger_station_gpsToDisk.mat'));
load(sprintf('%s/%s', path, 'route_gpsLogger_station_tapByteVector1.mat'));

load(sprintf('%s/%s', path, 'route_sound_station_sound.mat'));
load(sprintf('%s/%s', path, 'route_sound_station_syncQueue1.mat'));
load(sprintf('%s/%s', path, 'route_sound_station_todisk.mat'));
load(sprintf('%s/%s', path, 'route_sound_station_tapByteVector0.mat'));

xlabels = {'waiting', 'execution'};
%xlabels = {'RT waiting','thread waiting', 'RT execution','thread execution'}%, 'user', 'system'}

% route_fileMonitor_station_fileMonitor
passes = size(route_fileMonitor_station_fileMonitor);
figure('name', 'route_fileMonitor_station_fileMonitor');
boxplot(route_fileMonitor_station_fileMonitor./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('fileMonitor on fileMonitor Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_fileMonitor_station_fileMonitor.png'), 'png');

% route_fileMonitor_station_form
passes = size(route_fileMonitor_station_form);
figure('name', 'route_fileMonitor_station_form');
boxplot(route_fileMonitor_station_form./1000, xlabels, 'notch', 'on')
ylabel('time(us)');
title(strcat('form on fileMonitor Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_fileMonitor_station_form.png'),'png');

% route_fileMonitor_station_uploadComplete
passes = size(route_fileMonitor_station_uploadComplete);
figure('name', 'route_fileMonitor_station_uploadComplete');
boxplot(route_fileMonitor_station_uploadComplete./1000000, xlabels, 'notch', 'on');
ylabel('time(ms)');
title(strcat('uploadComplete on fileMonitor Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_fileMonitor_station_uploadComplete.png'),'png');

% route_fileMonitor_station_tapFilenameType0
passes = size(route_fileMonitor_station_tapFilenameType0);
figure('name', 'route_fileMonitor_station_tapFilenameType0');
boxplot(route_fileMonitor_station_tapFilenameType0./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('tapFilenameType0 on fileMonitor Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_fileMonitor_station_tapFilenameType0.png'),'png');

%%%%%%%%%%%%%%%%%%%%%%%%%%%

% route_form_station_form
passes = size(route_form_station_form);
figure('name', 'route_form_station_form');
boxplot(route_form_station_form./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('form on form Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_form_station_form.png'),'png');

% route_form_station_httpUpload
passes = size(route_form_station_httpUpload);
figure('name', 'route_form_station_httpUpload');
boxplot(route_form_station_httpUpload./1000000, xlabels, 'notch', 'on');
ylabel('time(ms)');
title(strcat('httpUpload on form Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_form_station_httpUpload.png'),'png');

% route_form_station_uploadComplete
passes = size(route_form_station_uploadComplete);
figure('name', 'route_form_station_uploadComplete');
boxplot(route_form_station_uploadComplete./1000000, xlabels, 'notch', 'on');
ylabel('time(ms)');
title(strcat('uploadComplete on form Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_form_station_uploadComplete.png'),'png');

% route_form_station_tapHTMLFormMessage0
passes = size(route_form_station_tapHTMLFormMessage0);
figure('name', 'route_form_station_tapHTMLFormMessage0');
boxplot(route_form_station_tapHTMLFormMessage0./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('tapHTMLFormMessage0 on form Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_form_station_tapHTMLFormMessage0.png'),'png');

%%%%%%%%%%%%%%%%%%%%%%%%%%%

% route_gps_station_gps
passes = size(route_gps_station_gps);
figure('name', 'route_gps_station_gps');
boxplot(route_gps_station_gps./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('gps on gps Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gps_station_gps.png'),'png');

% route_gps_station_gpsLogger
passes = size(route_gps_station_gpsLogger);
figure('name', 'route_gps_station_gpsLogger');
boxplot(route_gps_station_gpsLogger./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('gps-logger on gps Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gps_station_gpsLogger.png'),'png');

% route_gps_station_tapGPSMessage0
passes = size(route_gps_station_tapGPSMessage0);
figure('name', 'route_gps_station_tapGPSMessage0');
boxplot(route_gps_station_tapGPSMessage0./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('tapGPSMessage0 on gps Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gps_station_tapGPSMessage0.png'),'png');

%%%%%%%%%%%%%%%%%%%%%%%%%%%

% route_gpsLogger_station_gpsLogger
passes = size(route_gps_station_gps);
figure('name', 'route_gpsLogger_station_gpsLogger');
boxplot(route_gpsLogger_station_gpsLogger./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('gpsLogger on gpsLogger Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gpsLogger_station_gpsLogger.png'),'png');

% route_gpsLogger_station_syncQueue0
passes = size(route_gpsLogger_station_syncQueue0);
figure('name', 'route_gpsLogger_station_syncQueue0');
boxplot(route_gpsLogger_station_syncQueue0./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('syncQueue0 on gpsLogger Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gpsLogger_station_syncQueue0.png'),'png');

% route_gpsLogger_station_gpsToDisk
passes = size(route_gpsLogger_station_gpsToDisk);
figure('name', 'route_gpsLogger_station_gpsToDisk');
boxplot(route_gpsLogger_station_gpsToDisk./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('gpsToDisk on gpsLogger Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gpsLogger_station_gpsToDisk.png'),'png');

% route_gpsLogger_station_tapByteVector1
passes = size(route_gpsLogger_station_tapByteVector1);
figure('name', 'route_gpsLogger_station_tapByteVector1');
boxplot(route_gpsLogger_station_tapByteVector1./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('tapByteVector1 on gpsLogger Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_gpsLogger_station_tapByteVector1.png'),'png');

%%%%%%%%%%%%%%%%%%%%%%%%%%%

% route_sound_station_sound
passes = size(route_sound_station_sound);
figure('name', 'route_sound_station_sound');
boxplot(route_sound_station_sound./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('sound on sound Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_sound_station_sound.png'),'png');

% route_sound_station_syncQueue1
passes = size(route_sound_station_syncQueue1);
figure('name', 'route_sound_station_syncQueue1');
boxplot(route_sound_station_syncQueue1./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('syncQueue1 on sound Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_sound_station_syncQueue1.png'),'png');

% route_sound_station_todisk
passes = size(route_sound_station_todisk);
figure('name', 'route_sound_station_todisk');
boxplot(route_sound_station_todisk./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('todisk on sound Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_sound_station_todisk.png'),'png');

% route_sound_station_tapByteVector0
passes = size(route_sound_station_tapByteVector0);
figure('name', 'route_sound_station_tapByteVector0');
boxplot(route_sound_station_tapByteVector0./1000, xlabels, 'notch', 'on');
ylabel('time(us)');
title(strcat('tapByteVector0 on sound Route<', num2str(passes(1)), '>'));
saveas(gcf, sprintf('%s/%s', path, 'route_sound_station_tapByteVector0.png'),'png');
end







