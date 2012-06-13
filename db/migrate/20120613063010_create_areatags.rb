class CreateAreatags < ActiveRecord::Migration
  def change
    create_table :areatags do |t|
      t.integer :x
      t.integer :y
      t.integer :height
      t.integer :width
      t.integer :photo_id
      t.string :description

      t.timestamps
    end
  end
end
