# Getting Started Guide

## Introduction

Mastercard’s Community Pass Platform facilitates life transactions for the digitally excluded and underserved through providing a shared, interoperable digital platform that service providers can utilize to deliver their solutions.

## What you Will Learn

This Getting Started Guide Tutorial will lead you from the point of setting up your device and development environments until the beginning of your engagement with Community Pass Platform services. We will take you through these steps in the following seven (7) sections:

+ [Section 1](pre-requisites.md): Pre-requisites required to get you starting
+ [Section 2](device-setup.md): Setting up your Community Pass Approved Device
+ [Section 3](dev-environment-setup.md): Setting up your development environment
+ [Section 4](submit-app-details.md): Submit your Reliant App details to Community Pass
+ [Section 5](add-the-wrapper.md): Add the Community Pass Flutter Wrapper
+ [Section 6](install-activate-cpk.md): Install and Activate the CPK
+ [Section 7](connect-reliant-app.md): Register and connect your Flutter Reliant App with the Community Pass Flutter Wrapper

## Starting Community Pass Concepts

A brief introduction on some core Community Pass concepts below.

### Community Pass Components

From a technology point of view, there are three components that are needed to create a Community Pass integrated solution:

1. **Reliant Application**: An Android application developed and managed by the service provider which “relies” on the Community Pass Platform.
2. **Point of Interaction (POI) Device**: A Community Pass Approved Android device which has the Community Pass Software Development Kit (SDK) installed.
3. **Multi-Wallet Service Device**: S digital data store that hosts the Community Pass wallet. This Multi-Wallet service is provided in two types of form factors 
    1. A “chip” on a card or
    2. A QR code.

This tutorial will walk you through how to create your “Reliant” Application, integrate it with Community Pass, and how to obtain and configure the POI and Multi-Wallet devices.

### Community Pass SDK

The Community Pass SDK comes with three elements:

1. **Community Pass Kernel (CPK)**: The Community Pass Kernel is a headless Android application that runs in the background on your device (i.e. the POI). The “Reliant Application” will utilize this CPK to issue Community Pass commands. You will obtain and install/activate the CPK by receiving an .APK file (Android Package Kit) and activation code from the Community Pass team.
2. **Community Pass Flutter Wrapper**: The Community Pass Flutter Wrapper, distributed via an .AAR file (Android Archive Resource), is what you will embed in your Flutter Reliant Application in order to issue commands to the CPK which in turn executes Community Pass commands.
3. **Reference application**: To aid your understanding of this documentation, we have provided a Reference application and sample code available for download. During the course of this documentation, we will walk you through downloading and running the reference app which you can then use as example code as you set-up your Reliant application.
The steps on how to gain access to the Community Pass SDK’s are provided in the next Section (1) of this guide

### Anatomy of a CP Solution

At the highest level, a CP solution consists of an Android client app (Reliant Application) on a CP POI device that has the CPK installed. The CPK manages communication between the device and a Multi-Wallet service (found on a card in the example below), and between the device and CP backend services.

![Figure 1: Community Pass Devices interaction with Reliant Application](anatomy-of-cp-solution.png)

[Next Section](pre-requisites.md)
