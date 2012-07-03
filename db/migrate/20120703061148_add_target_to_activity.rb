class AddTargetToActivity < ActiveRecord::Migration
  def change
    add_column :activities, :target_id, :integer

    add_column :activities, :target_type, :string

    add_column :activities, :owner_id, :integer

    add_column :activities, :owner_type, :string

    add_index :activities, [:target_id, :target_type]
    add_index :activities, [:owner_id, :owner_type]

  end
end
