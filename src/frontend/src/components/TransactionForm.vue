<template>
  <a-button type="primary" @click="showDrawer">REGISTRAR</a-button>

  <a-drawer
    v-model:visible="visible"
    class="custom-class"
    style="color: red"
    title="TRANSACCIÓN"
    placement="right"
    @after-visible-change="afterVisibleChange"
  >
    <a-form
      :model="object"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      autocomplete="off"
      :validate-messages="validateMessages"
    >
      <a-form-item label="DESCRIPCIÓN" name="description">
        <a-textarea v-model:value="object.description" />
      </a-form-item>
      <a-form-item label="EMPLEADO" name="employee">
        <a-select
          label="Empleado"
          ref="select"
          v-model:value="object.employeeId"
          style="width: 150px"
        >
          <a-select-option
            v-for="employee in employees"
            :index="employee.id"
            :value="employee.id"
          >
            {{ employee.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="EQUIPO" name="equipment">
        <a-select
          label="Equipo"
          ref="select"
          v-model:value="object.equipment.id"
          style="width: 150px"
        >
          <a-select-option
            v-for="equipment in equipments"
            :index="equipment.id"
            :value="equipment.equipmentId"
          >
            {{ equipment.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="PROCECEDENCIA:" name="provenance">
        <a-select
          label="Procedencia"
          ref="select"
          v-model:value="object.locationProvenance"
          style="width: 150px"
        >
          <a-select-option
            :value="object.locationProvenance"
          >
          {{ locationRow }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="FILA DESTINO" name="destination">
        <a-select
          label="Destino"
          ref="select"
          v-model:value="object.locationDestination"
          style="width: 150px"
        >
          <a-select-option
            v-for="location in availableLocations"
            :index="location.id"
            :value="location.id"
          >
            {{ location.row }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :wrapper-col="{ span: 14, offset: 4 }">
        <a-button type="primary" @click="submitObject">Registrar</a-button>
        <a-button style="margin-left: 10px" @click="hideDrawer"
          >Cancelar</a-button
        >
      </a-form-item>
    </a-form>
  </a-drawer>
</template>

<script setup>
import { ref, reactive, watch } from "vue";
import { addNewTransaction } from "../composables/Transaction";
import {
  getLocationById,
  getLocationsByShelfId,
} from "../composables/Location";
import {
  successNotification,
  errorNotification,
} from "../composables/Notification";
import { updateEquipment, getEquipmentById } from "../composables/Equipment";

const emit = defineEmits(["update-list"]);

const props = defineProps({
  employees: { type: Array, required: true },
  equipments: { type: Array, required: true },
});

const visible = ref(false);

const object = reactive({
  description: "",
  equipment: { id: ""},
  employeeId: null,
  locationProvenance: null,
  locationDestination: null,
});

const locationRow = ref(null);

watch(object.equipment, async (newValue, oldValue) => {
  const equipmentId = object.equipment.id ? object.equipment.id : 0;
  console.log(equipmentId);
  // Obtiene kis datos del equipo seleccionado
  const equipmentSelected = props.equipments.find(
    (item) => item.equipmentId == equipmentId
  );
  if (equipmentSelected) {
    object.locationProvenance = equipmentSelected.locationId; // Colocando el ID de la ubicación de procedencia
    locationRow.value = equipmentSelected.shelfRow;// Colocando el la ubicación de procedencia
    let res = await getLocationById(equipmentSelected.locationId);
    let data = await res.json();
    const shelfId = data.shelf.id;
    console.log(shelfId);
    res = await getLocationsByShelfId(shelfId);
    data = await res.json();
    //mostrar todas las filas de dicho estante menos la actual
    availableLocations.value = data.filter(
      (item) => item.row !== equipmentSelected.shelfRow
    );
  }
});

const availableLocations = ref([]);

const submitObject = async () => {
  const item = { ...object }; // this is to prevent reactivity
  // adecuando el objeto a enviar
  item["equipmentId"] = item.equipment.id;
  delete item.equipment;

  try {
    const res = await getEquipmentById(item.equipmentId);
    const equipmentFound = await res.json();

    // actualizando la ubicación del equipop seleccionado
    equipmentFound.locationId = item.locationDestination;
    await updateEquipment(equipmentFound, equipmentFound.id);
    
    await addNewTransaction(item);
    successNotification(
      "Registro exitoso",
      `La transacción ha sido registrada.`
    );
    emit("update-list");
  } catch (e) {
    if (e.response) {
      const data = await e.response.json();
      if (data.errors) {
        errorNotification("Ocurrió un error :(", data.errors[0].defaultMessage);
        return;
      }
      errorNotification("Ocurrió un error al registrar:(", data.message);
    }
  } finally {
    hideDrawer();
    cleanObject();
  }
};

const afterVisibleChange = (bool) => {
  console.log("visible", bool);
};
const showDrawer = () => {
  visible.value = true;
};
const hideDrawer = () => {
  visible.value = false;
};
const cleanObject = () => {
  object.description = "";
  object.equipment.id = null;
  object.employeeId = null;
  object.locationProvenance = null;
  object.locationDestination = null;
};

const validateMessages = {
  required: "${label} is required!",
  types: {
    email: "${label} no es un email válido!",
    number: "${label} is not a valid number!",
  },
  number: {
    range: "${label} must be between ${min} and ${max}",
  },
};
</script>

<style scoped>
</style>