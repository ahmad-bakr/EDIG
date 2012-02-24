
UWCAN_alpha =   [0.0,     0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
UWCAN_error =[1-0.607, 1-0.837, 1-0.837, 1-0.908, 1-0.870, 1-0.859, 1-0.906, 1-0.908, 1-0.920, 1-0.917, 1-0.884];

universities_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
universities_error = [1-0.408, 1-0.420, 1-0.444, 1-0.515, 1-0.516, 1-0.625, 1-0.690, 1-0.764, 1-0.672, 1-0.672, 1-0.576];

syskill_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
syskill_error = [1-0.321, 1-0.814, 1-0.821, 1-0.946, 1-0.913, 1-0.946, 1-0.981, 1-0.981, 1-0.981, 1-0.981, 1-0.969];

reuters_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
reuters_error = [1-0.399, 1-0.471, 1-0.568, 1-0.615, 1-0.611, 1-0.612, 1-0.647, 1-0.630, 1-0.629, 1-0.642, 1-0.636];

news_alpha =    [0.0,   0.1,   0.2,   0.3,   0.4,   0.5,   0.6,   0.7,   0.8,   0.9,   1.0];
news_error = [1-0.554, 1-0.765, 1-0.795, 1-0.817, 1-0.801, 1-0.801, 1-0.831, 1-0.771, 1-0.825, 1-0.825, 1-0.808];


plot(UWCAN_alpha,UWCAN_error,'-+k','LineWidth',1.4);
hold on
plot(universities_alpha,universities_error,'-.ok','LineWidth',1.4);
hold on
plot(syskill_alpha,syskill_error,':*k','LineWidth',1.4);
hold on
plot(reuters_alpha, reuters_error,'--vk','LineWidth',1.4);
hold on
plot(news_alpha, news_error,'-*k','LineWidth',1.4);
hold on
leg = legend('UW-CAN','4 Universities', 'Syskill', 'Reuters', '20 News Group');
set(leg,'Location','NorthEast');
hold off

title('Alpha - Error Plot for The Five Datasets');
xlabel('Alpha');
ylabel('Error');
