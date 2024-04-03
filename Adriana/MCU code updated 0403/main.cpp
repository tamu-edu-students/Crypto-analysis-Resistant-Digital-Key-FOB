/*
 * SPDX-FileCopyrightText: 2021-2022 Espressif Systems (Shanghai) CO LTD
 *
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 */

#include <iostream>
#include <string>
#include <cmath>
#include <stdint.h>
#include <string.h>
#include <stdbool.h>
#include <stdio.h>
#include <inttypes.h>
#include "nvs.h"
#include "nvs_flash.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"
#include "esp_bt.h"
#include "esp_bt_main.h"
#include "esp_gap_bt_api.h"
#include "esp_bt_device.h"
#include "esp_spp_api.h"
#include "driver/gpio.h"

using namespace std;
//using std::string;
//using std::to_string;

#include "time.h"
#include "sys/time.h"

#define SPP_TAG "SPP_ACCEPTOR_DEMO"
#define SPP_SERVER_NAME "SPP_SERVER"
#define EXAMPLE_DEVICE_NAME "ESP_BLUETOOTH_TEST"
#define SPP_SHOW_DATA 0
#define SPP_SHOW_SPEED 1
#define SPP_SHOW_MODE SPP_SHOW_DATA   /*Choose show mode: show data or speed*/

#define MAX 100000
#define ledG GPIO_NUM_5
#define ledR GPIO_NUM_18

const char *tag = "Bluetooth";

static const esp_spp_mode_t esp_spp_mode = ESP_SPP_MODE_CB;
static const bool esp_spp_enable_l2cap_ertm = true;

static struct timeval time_new, time_old;
static long data_num = 0;

static const esp_spp_sec_t sec_mask = ESP_SPP_SEC_AUTHENTICATE;
static const esp_spp_role_t role_slave = ESP_SPP_ROLE_SLAVE;

esp_bd_addr_t peer_bd_addr = {0};
static uint8_t peer_bdname_len;
static char peer_bdname[ESP_BT_GAP_MAX_BDNAME_LEN + 1];

//Initiator Code Start
#if (SPP_SHOW_MODE == SPP_SHOW_DATA)
#define SPP_DATA_LEN 20
#else
#define SPP_DATA_LEN ESP_SPP_MAX_MTU
#endif
static uint8_t spp_data[SPP_DATA_LEN];
static uint8_t *s_p_data = NULL; /* data pointer of spp_data */
//Initiator Code End

//Functions to Enable C++ to Do Power and Mod Mathematics with Larger Numbers
//DHKE
int multiply (int x, int res[], int res_size){
    /* Function does multiplication one array element at a time
    as to accomodate larger numbers. */
    int carry = 0;
    for (int i = 0; i < res_size; i++){
        int prod = res[i] * x + carry;
        res[i] = prod % 10;
        carry = prod / 10;
    }

    while (carry) {
        res[res_size]  = carry % 10;
        carry = carry/10;
        res_size++;
    }
    return res_size;
}

string power(int x, int n){
    /* Does the power manually to allow for larger numbers.
    The final result is put in a string to be used for the DHKE later. */
    if (n == 0){ //setting the resulting power to 1 is the input is 0
        string answer;
        answer = "1";
        return answer;
    }

    int res[MAX];
    int res_size = 0;
    int temp = x;

    while (temp !=0) {
        res[res_size++] = temp % 10;
        temp = temp / 10;
    }

    for (int i = 2; i <= n; i++)
        res_size = multiply(x, res, res_size);
    
    string result;
    for (int i = res_size - 1; i >= 0; i--){
        result = result + to_string(res[i]);
    }
    return result;
}

int mod (string num, int a){ //Does the Modulus Function for larger numbers
    int res = 0;
    for (int i = 0; i < num.length(); i++)
        res = (res * 10 + num[i] - '0') % a; //Property that allows for easier modulating of numbers
    
    return res;
}

//LED Control
static void init_led(void)
{
    gpio_reset_pin(ledR);
    gpio_set_direction(ledR, GPIO_MODE_OUTPUT);
    gpio_reset_pin(ledG);
    gpio_set_direction(ledG, GPIO_MODE_OUTPUT);

    ESP_LOGI(tag, "Init led completed");   
}

static char *bda2str(uint8_t * bda, char *str, size_t size)
{
    if (bda == NULL || str == NULL || size < 18) {
        return NULL;
    }

    uint8_t *p = bda;
    sprintf(str, "%02x:%02x:%02x:%02x:%02x:%02x",
            p[0], p[1], p[2], p[3], p[4], p[5]);
    return str;
}

static void print_speed(void)
{
    float time_old_s = time_old.tv_sec + time_old.tv_usec / 1000000.0;
    float time_new_s = time_new.tv_sec + time_new.tv_usec / 1000000.0;
    float time_interval = time_new_s - time_old_s;
    float speed = data_num * 8 / time_interval / 1000.0;
    ESP_LOGI(SPP_TAG, "speed(%fs ~ %fs): %f kbit/s" , time_old_s, time_new_s, speed);
    data_num = 0;
    time_old.tv_sec = time_new.tv_sec;
    time_old.tv_usec = time_new.tv_usec;
}

static void esp_spp_cb(esp_spp_cb_event_t event, esp_spp_cb_param_t *param)
{
    char bda_str[18] = {0};

    switch (event) {
    case ESP_SPP_INIT_EVT:
        if (param->init.status == ESP_SPP_SUCCESS) {
            ESP_LOGI(SPP_TAG, "ESP_SPP_INIT_EVT");
            esp_spp_start_srv(sec_mask, role_slave, 0, SPP_SERVER_NAME);
        } else {
            ESP_LOGE(SPP_TAG, "ESP_SPP_INIT_EVT status:%d", param->init.status);
        }
        break;
    case ESP_SPP_DISCOVERY_COMP_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_DISCOVERY_COMP_EVT");
        break;
    case ESP_SPP_OPEN_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_OPEN_EVT");
        break;
    case ESP_SPP_CLOSE_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_CLOSE_EVT status:%d handle:%"PRIu32" close_by_remote:%d", param->close.status,
                 param->close.handle, param->close.async);
        break;
    case ESP_SPP_START_EVT:
        if (param->start.status == ESP_SPP_SUCCESS) {
            ESP_LOGI(SPP_TAG, "ESP_SPP_START_EVT handle:%"PRIu32" sec_id:%d scn:%d", param->start.handle, param->start.sec_id,
                     param->start.scn);
            esp_bt_dev_set_device_name(EXAMPLE_DEVICE_NAME);
            esp_bt_gap_set_scan_mode(ESP_BT_CONNECTABLE, ESP_BT_GENERAL_DISCOVERABLE);
        } else {
            ESP_LOGE(SPP_TAG, "ESP_SPP_START_EVT status:%d", param->start.status);
        }
        break;
    case ESP_SPP_CL_INIT_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_CL_INIT_EVT");
        break;
    case ESP_SPP_DATA_IND_EVT:
#if (SPP_SHOW_MODE == SPP_SHOW_DATA)
        /*
         * We only show the data in which the data length is less than 128 here. If you want to print the data and
         * the data rate is high, it is strongly recommended to process them in other lower priority application task
         * rather than in this callback directly. Since the printing takes too much time, it may stuck the Bluetooth
         * stack and also have a effect on the throughput!
         */
        ESP_LOGI(SPP_TAG, "ESP_SPP_DATA_IND_EVT len:%d handle:%d",
                 param->data_ind.len, param->data_ind.handle);
        if (param->data_ind.len < 128) {
            esp_log_buffer_hex("", param->data_ind.data, param->data_ind.len);
            //string data_received = static_cast<string>(*param->data_ind.data); //fix!!!!!!!!!!!
            string data_received(reinterpret_cast<char*>(param->data_ind.data), param->data_ind.len);

            printf("data_received string: %s\n", data_received.c_str());
            string process;
            //TO DO: Separate process plain text from signal 
            for (int i = 0; i < 3; i++){
                process.push_back(data_received[i]);
                printf("%s\n", process.c_str());
            }
            data_received.erase(0,3);
            printf("process string:%s\n", process.c_str());
            printf("new data_received string after removing process:%s", data_received.c_str());
            printf("starting if statement selection\n");

            //Actual DHKE process
            if (process == "DKE" /*process.compare("DKE") == 0)*/) {
                printf("Entered DKE process to start DiffieHellman\n");
                //DHKE 
                int P;
                P = 13;
                int G;
                G = 7;
                long long randInt;
                long long A; //TO DO: Seperate A' sent and store in A
                A = stoll(data_received, nullptr, 10); //converts string to long long int

                //Generating a random number B
                srand((unsigned) time(NULL));
                long long b = 1 + (rand() % 6);
                printf("b value is: %llu\n", b);

                //Doing the Diffie-Hellman Mathematics - G^b mod P
                long long Bprime;
                Bprime = static_cast<long long>(pow(G, b)) % P;
                string B_to_send = to_string(Bprime);
                printf("Bprime: %s\n", B_to_send.c_str());

                //send Bprime to app
                param->data_ind.len = B_to_send.length();
                //param->data_ind.data = reinterpret_cast<const uint8_t*>(B_to_send.c_str());
                // Assuming param->data_ind.data is a modifiable buffer
                // Allocate memory for the buffer
                param->data_ind.data = new uint8_t[B_to_send.length()];

                // Copy the data from B_to_send to param->data_ind.data
                std::copy(B_to_send.begin(), B_to_send.end(), param->data_ind.data);

                //esp_spp_write(param->data_ind.handle, B_to_send.length(), B_to_send); DOESNT WORK
                esp_spp_write(param->data_ind.handle, param->data_ind.len, param->data_ind.data);
                //check to see if handle works?

                long long sk;
                sk = static_cast<long long>(pow(A,b)) % P;
                printf("Secret key: %llu\n", sk);

                esp_err_t err = nvs_flash_init();
                if (err == ESP_ERR_NVS_NO_FREE_PAGES || err == ESP_ERR_NVS_NEW_VERSION_FOUND) {
                    // NVS partition was truncated and needs to be erased
                    // Retry nvs_flash_init
                    ESP_ERROR_CHECK(nvs_flash_erase());
                    err = nvs_flash_init();
                }
                ESP_ERROR_CHECK( err );

                nvs_handle_t DiffieHellman;
                err = nvs_open("storage", NVS_READWRITE, &DiffieHellman);
                if (err != ESP_OK) {
                    printf("Error (%s) opening NVS handle!\n", esp_err_to_name(err));
                } else {
                    printf("Done\n");

                    //write sk into nvs
                    err = nvs_set_i32(DiffieHellman, "secret key", sk);
                    printf((err != ESP_OK) ? "Failed!\n" : "Done\n");

                    //commit written value
                    err = nvs_commit(DiffieHellman);
                    printf((err != ESP_OK) ? "Failed!\n" : "Done\n");

                    //close
                    nvs_close(DiffieHellman);
                }
            }
            else if (process == "AES"){
                printf("Entered AES\n");
                //AES is when z-hardware profile comes in as a plaintext

                //Storing Z-Hardware profile in Non-Volatile Storage
                //Initialize NVS
                esp_err_t err = nvs_flash_init();
                if (err == ESP_ERR_NVS_NO_FREE_PAGES || err == ESP_ERR_NVS_NEW_VERSION_FOUND) {
                    // NVS partition was truncated and needs to be erased
                    // Retry nvs_flash_init
                    ESP_ERROR_CHECK(nvs_flash_erase());
                    err = nvs_flash_init();
                }
                ESP_ERROR_CHECK( err );

                //Open NVS

                string handle_name; //vehicle plain text *TO DO: assign variable
                string z_hardware;
                int pos = data_received.find("|"); 
                z_hardware.assign(data_received.substr(0,pos)); //z-hardware profile plaintext is the beginning characters of data_received
                printf("z-hardware:%s\n", z_hardware.c_str());
                handle_name.assign(data_received.substr(pos+1, data_received.length())); //handle name is last characters of data_received
                printf("handle name:%s\n", handle_name.c_str());
                nvs_handle_t nvs_handle = strtoul(handle_name.c_str(), nullptr, 0); 
                //nvs_handle_t nvs_handle;
                err = nvs_open("storage", NVS_READWRITE, &nvs_handle);
                if (err != ESP_OK) {
                    printf("Error (%s) opening NVS handle!\n", esp_err_to_name(err));
                } else {
                    printf("Done\n");

                    //write z-hardware into nvs
                    err = nvs_set_str(nvs_handle, "z hardware", z_hardware.c_str());
                    printf((err != ESP_OK) ? "Failed!\n" : "Done\n"); //fails?
                    printf("error: %d\n", err);

                    //commit written value
                    err = nvs_commit(nvs_handle);
                    printf((err != ESP_OK) ? "Failed!\n" : "Done\n");

                    //close
                    nvs_close(nvs_handle);
                }
            }
            else if (process == "COM") {

                printf("Value received: ");
                for (size_t i = 0; i < (data_received.length()) - 2; i++)
                {
                    char value = data_received[i];
                    printf("%c", value);

                    switch(value)
                    {
                    case '0': //Red LED OFF
                        gpio_set_level(ledR, 0);
                        break;
                    case '1': //Red LED ON
                        gpio_set_level(ledR, 1);
                        break;
                    case '2': //Green LED OFF
                        gpio_set_level(ledG, 0);
                        break;
                    case '3': //Green LED ON
                        gpio_set_level(ledG, 1);
                        break;
                    default: //default Green and Red LED both OFF
                        gpio_set_level(ledG, 0);
                        gpio_set_level(ledR, 0); 
                        break;
                    }
                }
                printf("\n");

                //esp_spp_write(param->data_ind.handle, param->data_ind.len, param->data_ind.data);
            }
            else{
                printf("Entered else; skipped all if/else if statements");
            }
            //TO DO: clear all variables when done with computation <- maybe doesnt have to be done?
        }
#else
        gettimeofday(&time_new, NULL);
        data_num += param->data_ind.len;
        if (time_new.tv_sec - time_old.tv_sec >= 3) {
            print_speed();
        }
#endif
        break;
    case ESP_SPP_CONG_EVT:
/*#if (SPP_SHOW_MODE == SPP_SHOW_DATA)
        ESP_LOGI(SPP_TAG, "ESP_SPP_CONG_EVT cong:%d", param->cong.cong);
#endif
        if (param->cong.cong == 0) {
            / Send the privous (partial) data packet or the next data packet. */
           /* esp_spp_write(param->write.handle, spp_data + SPP_DATA_LEN - s_p_data, s_p_data);
        }
        break;*/
        ESP_LOGI(SPP_TAG, "ESP_SPP_CONG_EVT");
        break;
    case ESP_SPP_WRITE_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_WRITE_EVT");
        break;
    case ESP_SPP_SRV_OPEN_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_SRV_OPEN_EVT status:%d handle:%"PRIu32", rem_bda:[%s]", param->srv_open.status,
                 param->srv_open.handle, bda2str(param->srv_open.rem_bda, bda_str, sizeof(bda_str)));
        gettimeofday(&time_old, NULL);
        break;
    case ESP_SPP_SRV_STOP_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_SRV_STOP_EVT");
        break;
    case ESP_SPP_UNINIT_EVT:
        ESP_LOGI(SPP_TAG, "ESP_SPP_UNINIT_EVT");
        break;
    default:
        break;
    }
}

void esp_bt_gap_cb(esp_bt_gap_cb_event_t event, esp_bt_gap_cb_param_t *param)
{
    char bda_str[18] = {0};

    switch (event) {
    case ESP_BT_GAP_AUTH_CMPL_EVT:{
        if (param->auth_cmpl.stat == ESP_BT_STATUS_SUCCESS) {
            ESP_LOGI(SPP_TAG, "authentication success: %s bda:[%s]", param->auth_cmpl.device_name,
                     bda2str(param->auth_cmpl.bda, bda_str, sizeof(bda_str)));
        } else {
            ESP_LOGE(SPP_TAG, "authentication failed, status:%d", param->auth_cmpl.stat);
        }
        break;
    }
    case ESP_BT_GAP_PIN_REQ_EVT:{
        ESP_LOGI(SPP_TAG, "ESP_BT_GAP_PIN_REQ_EVT min_16_digit:%d", param->pin_req.min_16_digit);
        if (param->pin_req.min_16_digit) {
            ESP_LOGI(SPP_TAG, "Input pin code: 0000 0000 0000 0000");
            esp_bt_pin_code_t pin_code = {0};
            esp_bt_gap_pin_reply(param->pin_req.bda, true, 16, pin_code);
        } else {
            ESP_LOGI(SPP_TAG, "Input pin code: 1234");
            esp_bt_pin_code_t pin_code;
            pin_code[0] = '1';
            pin_code[1] = '2';
            pin_code[2] = '3';
            pin_code[3] = '4';
            esp_bt_gap_pin_reply(param->pin_req.bda, true, 4, pin_code);
        }
        break;
    }

#if (CONFIG_BT_SSP_ENABLED == true)
    case ESP_BT_GAP_CFM_REQ_EVT:
        ESP_LOGI(SPP_TAG, "ESP_BT_GAP_CFM_REQ_EVT Please compare the numeric value: %"PRIu32, param->cfm_req.num_val);
        esp_bt_gap_ssp_confirm_reply(param->cfm_req.bda, true);
        break;
    case ESP_BT_GAP_KEY_NOTIF_EVT:
        ESP_LOGI(SPP_TAG, "ESP_BT_GAP_KEY_NOTIF_EVT passkey:%"PRIu32, param->key_notif.passkey);
        break;
    case ESP_BT_GAP_KEY_REQ_EVT:
        ESP_LOGI(SPP_TAG, "ESP_BT_GAP_KEY_REQ_EVT Please enter passkey!");
        break;
#endif

    case ESP_BT_GAP_MODE_CHG_EVT:
        ESP_LOGI(SPP_TAG, "ESP_BT_GAP_MODE_CHG_EVT mode:%d bda:[%s]", param->mode_chg.mode,
                 bda2str(param->mode_chg.bda, bda_str, sizeof(bda_str)));
        break;

    default: {
        ESP_LOGI(SPP_TAG, "event: %d", event);
        break;
    }
    }
    return;
}

extern "C" void app_main(void)
{
    init_led();
    esp_err_t ret = ESP_OK;
    char bda_str[18] = {0};

    for (int i = 0; i < SPP_DATA_LEN; ++i) {
        spp_data[i] = i;
    }

    ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK( ret );

    ESP_ERROR_CHECK(esp_bt_controller_mem_release(ESP_BT_MODE_BLE));

    esp_bt_controller_config_t bt_cfg = BT_CONTROLLER_INIT_CONFIG_DEFAULT();
    if ((ret = esp_bt_controller_init(&bt_cfg)) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s initialize controller failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    if ((ret = esp_bt_controller_enable(ESP_BT_MODE_CLASSIC_BT)) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s enable controller failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    if ((ret = esp_bluedroid_init()) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s initialize bluedroid failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    if ((ret = esp_bluedroid_enable()) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s enable bluedroid failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    if ((ret = esp_bt_gap_register_callback(esp_bt_gap_cb)) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s gap register failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    if ((ret = esp_spp_register_callback(esp_spp_cb)) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s spp register failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

    esp_spp_cfg_t bt_spp_cfg = {
        .mode = esp_spp_mode,
        .enable_l2cap_ertm = esp_spp_enable_l2cap_ertm,
        .tx_buffer_size = 0, /* Only used for ESP_SPP_MODE_VFS mode */
    };
    if ((ret = esp_spp_enhanced_init(&bt_spp_cfg)) != ESP_OK) {
        ESP_LOGE(SPP_TAG, "%s spp init failed: %s\n", __func__, esp_err_to_name(ret));
        return;
    }

#if (CONFIG_BT_SSP_ENABLED == true)
    /* Set default parameters for Secure Simple Pairing */
    esp_bt_sp_param_t param_type = ESP_BT_SP_IOCAP_MODE;
    esp_bt_io_cap_t iocap = ESP_BT_IO_CAP_IO;
    esp_bt_gap_set_security_param(param_type, &iocap, sizeof(uint8_t));
#endif

    /*
     * Set default parameters for Legacy Pairing
     * Use variable pin, input pin code when pairing
     */
    esp_bt_pin_type_t pin_type = ESP_BT_PIN_TYPE_VARIABLE;
    esp_bt_pin_code_t pin_code;
    esp_bt_gap_set_pin(pin_type, 0, pin_code);

    ESP_LOGI(SPP_TAG, "Own address:[%s]", bda2str((uint8_t *)esp_bt_dev_get_address(), bda_str, sizeof(bda_str)));
}
