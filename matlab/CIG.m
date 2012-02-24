
UWCAN_alpha =   [0.0,     0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
UWCAN_fmeasure =[0.607, 0.837, 0.837, 0.908, 0.870, 0.859, 0.906, 0.908, 0.920, 0.917, 0.884];

universities_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
universities_fmeasure = [0.408, 0.420, 0.444, 0.515, 0.516, 0.625, 0.690, 0.764, 0.672, 0.672, 0.576];

syskill_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
syskill_fmeasure = [0.321, 0.814, 0.821, 0.946, 0.913, 0.946, 0.981, 0.981, 0.981, 0.981, 0.969];

reuters_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
reuters_fmeasure = [0.399, 0.471, 0.568, 0.615, 0.611, 0.612, 0.647, 0.630, 0.629, 0.642, 0.636];

news_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
news_fmeasure = [0.554, 0.765, 0.795, 0.817, 0.801, 0.801, 0.831, 0.771, 0.825, 0.825, 0.808];


plot(UWCAN_alpha,UWCAN_fmeasure,'-+k','LineWidth',1.4);
hold on
plot(universities_alpha,universities_fmeasure,'-.ok','LineWidth',1.4);
hold on
plot(syskill_alpha,syskill_fmeasure,':*k','LineWidth',1.4);
hold on
plot(reuters_alpha, reuters_fmeasure,'--vk','LineWidth',1.4);
hold on
plot(news_alpha, news_fmeasure,'-*k','LineWidth',1.4);
hold on
leg = legend('UW-CAN','4 Universities', 'Syskill', 'Reuters', '20 News Group');
set(leg,'Location','SouthEast');
hold off

title('Alpha - F Measure Plot for The Five Datasets');
xlabel('Alpha');
ylabel('F Measure');
